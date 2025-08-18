import {
  S3Client,
  ListObjectsV2Command,
  GetObjectCommand,
} from "@aws-sdk/client-s3";

// AWS 설정
const awsConfig = {
  region: "ap-northeast-2",
  credentials: {
    accessKeyId: "AKIAXZMEDYBHCGR7SCRL",
    secretAccessKey: "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",
  },
};

// S3 클라이언트 초기화
const s3Client = new S3Client(awsConfig);

export interface S3FileInfo {
  key: string;
  size: number;
  lastModified: Date;
  type: "video" | "image";
  memberId?: string;
  gameId?: string;
  uuid?: string;
  emotion?: string;
  number?: string;
}

export interface DataAnalysis {
  totalFiles: number;
  totalSize: number;
  memberCount: number;
  gameCount: number;
  videoCount: number;
  imageCount: number;
  emotionDistribution: Record<string, number>;
  gameDistribution: Record<string, number>;
  memberDistribution: Record<string, number>;
  recentFiles: S3FileInfo[];
}

export class S3AnalyticsService {
  private bucketName = "zmnnoory-media";
  private prefix = "zmnnoory/";

  // S3에서 파일 목록 가져오기
  async listFiles(): Promise<S3FileInfo[]> {
    try {
      console.log("S3 접근 시도 중...", {
        bucket: this.bucketName,
        prefix: this.prefix,
      });

      const command = new ListObjectsV2Command({
        Bucket: this.bucketName,
        Prefix: this.prefix,
        MaxKeys: 1000, // 최대 1000개 파일
      });

      const response = await s3Client.send(command);

      console.log("S3 응답:", response);

      if (!response.Contents) {
        console.log("S3 버킷이 비어있거나 접근할 수 없습니다.");
        return [];
      }

      const mappedFiles = response.Contents.map((item: any) => {
        const key = item.Key || "";
        const fileInfo = this.parseFileKey(key);
        const fileType = this.getFileType(key);

        console.log(
          `파일 매핑: ${key} -> 타입: ${fileType}, 크기: ${item.Size}`
        );

        return {
          key,
          size: item.Size || 0,
          lastModified: item.LastModified || new Date(),
          type: fileType,
          ...fileInfo,
        };
      });

      console.log(`총 ${mappedFiles.length}개 파일 처리 완료`);
      console.log(
        `영상 파일: ${mappedFiles.filter((f) => f.type === "video").length}개`
      );
      console.log(
        `이미지 파일: ${mappedFiles.filter((f) => f.type === "image").length}개`
      );

      return mappedFiles;
    } catch (error: any) {
      console.error("S3 파일 목록 조회 실패:", error);
      console.error("에러 상세:", {
        message: error.message,
        code: error.code,
        statusCode: error.statusCode,
        requestId: error.requestId,
      });
      throw error;
    }
  }

  // 파일 키 파싱 (멤버ID/게임ID/UUID 또는 감정+번호+UUID)
  private parseFileKey(key: string): Partial<S3FileInfo> {
    // zmnnoory/ 제거
    const relativePath = key.replace(this.prefix, "");
    const parts = relativePath.split("/");

    if (parts.length >= 3) {
      const [memberId, gameId, filename] = parts;

      // 파일명에서 확장자 제거
      const nameWithoutExt = filename.split(".")[0];

      // 감정 추출 (파일명이 감정으로 시작하는 경우)
      const emotionMatch = nameWithoutExt.match(
        /^(angry|sad|happy|neutral|fearful|surprised|disgusted)(\d+)?_(.+)$/
      );

      if (emotionMatch) {
        const [, emotion, number, uuid] = emotionMatch;
        return {
          memberId,
          gameId,
          uuid,
          emotion,
          number,
        };
      } else {
        // 일반적인 형태 (UUID만)
        return {
          memberId,
          gameId,
          uuid: nameWithoutExt,
        };
      }
    }

    return {};
  }

  // 파일 타입 판별
  private getFileType(key: string): "video" | "image" {
    const extension = key.split(".").pop()?.toLowerCase();

    console.log(`파일 타입 판별: ${key} -> 확장자: ${extension}`);

    // 이미지 파일 확장자인 경우만 이미지로 처리
    if (
      ["jpg", "jpeg", "png", "gif", "bmp", "webp"].includes(extension || "")
    ) {
      console.log(`  -> 이미지 파일로 판별: ${extension}`);
      return "image";
    }

    // 그 외 모든 파일은 영상 파일로 처리
    console.log(`  -> 영상 파일로 판별: ${extension || "확장자 없음"}`);
    return "video";
  }

  // S3에서 이미지 데이터 가져오기 (Base64로 반환)
  async getImageData(key: string): Promise<string> {
    try {
      const command = new GetObjectCommand({
        Bucket: this.bucketName,
        Key: key,
      });

      const response = await s3Client.send(command);

      if (response.Body) {
        // 스트림을 ArrayBuffer로 변환
        const arrayBuffer = await response.Body.transformToByteArray();

        // ArrayBuffer를 Base64로 변환 (브라우저 호환 방식)
        const base64String = await this.arrayBufferToBase64(arrayBuffer);

        // 이미지 MIME 타입 추정
        const extension = key.split(".").pop()?.toLowerCase();
        let mimeType = "image/jpeg"; // 기본값

        switch (extension) {
          case "png":
            mimeType = "image/png";
            break;
          case "gif":
            mimeType = "image/gif";
            break;
          case "webp":
            mimeType = "image/webp";
            break;
          case "bmp":
            mimeType = "image/bmp";
            break;
          default:
            mimeType = "image/jpeg";
        }

        const dataUrl = `data:${mimeType};base64,${base64String}`;
        console.log(`이미지 데이터 로드 성공: ${key}`);
        return dataUrl;
      } else {
        throw new Error("응답 본문이 없습니다.");
      }
    } catch (error) {
      console.error(`이미지 데이터 로드 실패: ${key}`, error);
      // 폴백: 직접 S3 URL 반환
      return `https://${this.bucketName}.s3.${awsConfig.region}.amazonaws.com/${key}`;
    }
  }

  // 데이터 분석 수행
  async analyzeData(): Promise<DataAnalysis> {
    const files = await this.listFiles();

    const analysis: DataAnalysis = {
      totalFiles: files.length,
      totalSize: files.reduce((sum, file) => sum + file.size, 0),
      memberCount: new Set(files.map((f) => f.memberId).filter(Boolean)).size,
      gameCount: new Set(files.map((f) => f.gameId).filter(Boolean)).size,
      videoCount: files.filter((f) => f.type === "video").length,
      imageCount: files.filter((f) => f.type === "image").length,
      emotionDistribution: {},
      gameDistribution: {},
      memberDistribution: {},
      recentFiles: files
        .sort((a, b) => b.lastModified.getTime() - a.lastModified.getTime())
        .slice(0, 10),
    };

    // 멤버별 분포
    files.forEach((file) => {
      if (file.memberId) {
        analysis.memberDistribution[file.memberId] =
          (analysis.memberDistribution[file.memberId] || 0) + 1;
      }
    });

    // 게임별 분포
    files.forEach((file) => {
      if (file.gameId) {
        analysis.gameDistribution[file.gameId] =
          (analysis.gameDistribution[file.gameId] || 0) + 1;
      }
    });

    return analysis;
  }

  // 파일 타입별 통계 조회
  async getFileTypeStats(): Promise<{
    videos: S3FileInfo[];
    images: S3FileInfo[];
    videoCount: number;
    imageCount: number;
    videoTotalSize: number;
    imageTotalSize: number;
  }> {
    const files = await this.listFiles();
    const videos = files
      .filter((file) => file.type === "video")
      .sort((a, b) => b.lastModified.getTime() - a.lastModified.getTime());
    const images = files
      .filter((file) => file.type === "image")
      .sort((a, b) => b.lastModified.getTime() - a.lastModified.getTime());

    return {
      videos,
      images,
      videoCount: videos.length,
      imageCount: images.length,
      videoTotalSize: videos.reduce((sum, file) => sum + file.size, 0),
      imageTotalSize: images.reduce((sum, file) => sum + file.size, 0),
    };
  }

  // 날짜별 분석 데이터 생성
  async getDateAnalysis(days: number = 30): Promise<{
    labels: string[];
    videoData: number[];
    imageData: number[];
    totalData: number[];
  }> {
    const files = await this.listFiles();

    // 지정된 일수만큼의 데이터 수집
    const dateMap = new Map<string, { videos: number; images: number }>();

    // 지정된 일수만큼 날짜 초기화
    for (let i = days - 1; i >= 0; i--) {
      const date = new Date();
      date.setDate(date.getDate() - i);
      const dateStr = date.toISOString().split("T")[0]; // YYYY-MM-DD 형식
      dateMap.set(dateStr, { videos: 0, images: 0 });
    }

    // 파일별로 날짜 그룹핑
    files.forEach((file) => {
      const dateStr = file.lastModified.toISOString().split("T")[0];
      if (dateMap.has(dateStr)) {
        const current = dateMap.get(dateStr)!;
        if (file.type === "video") {
          current.videos++;
        } else {
          current.images++;
        }
      }
    });

    // 차트 데이터 형식으로 변환
    const labels: string[] = [];
    const videoData: number[] = [];
    const imageData: number[] = [];
    const totalData: number[] = [];

    dateMap.forEach((value, key) => {
      labels.push(key);
      videoData.push(value.videos);
      imageData.push(value.images);
      totalData.push(value.videos + value.images);
    });

    return { labels, videoData, imageData, totalData };
  }

  // 게임별 분석 데이터 생성
  async getGameAnalysis(): Promise<{
    labels: string[];
    videoData: number[];
    imageData: number[];
    totalData: number[];
    gameStats: Array<{
      gameId: string;
      videoCount: number;
      imageCount: number;
      totalSize: number;
    }>;
  }> {
    const files = await this.listFiles();

    // 게임별 통계 수집
    const gameMap = new Map<
      string,
      { videos: number; images: number; totalSize: number }
    >();

    files.forEach((file) => {
      if (file.gameId) {
        if (!gameMap.has(file.gameId)) {
          gameMap.set(file.gameId, { videos: 0, images: 0, totalSize: 0 });
        }

        const current = gameMap.get(file.gameId)!;
        if (file.type === "video") {
          current.videos++;
        } else {
          current.images++;
        }
        current.totalSize += file.size;
      }
    });

    // 차트 데이터 형식으로 변환
    const labels: string[] = [];
    const videoData: number[] = [];
    const imageData: number[] = [];
    const totalData: number[] = [];
    const gameStats: Array<{
      gameId: string;
      videoCount: number;
      imageCount: number;
      totalSize: number;
    }> = [];

    gameMap.forEach((value, key) => {
      labels.push(key);
      videoData.push(value.videos);
      imageData.push(value.images);
      totalData.push(value.videos + value.images);
      gameStats.push({
        gameId: key,
        videoCount: value.videos,
        imageCount: value.images,
        totalSize: value.totalSize,
      });
    });

    return { labels, videoData, imageData, totalData, gameStats };
  }

  // 모든 영상 파일 조회
  async getAllVideoFiles(): Promise<S3FileInfo[]> {
    const files = await this.listFiles();
    return files
      .filter((file) => file.type === "video")
      .sort((a, b) => b.lastModified.getTime() - a.lastModified.getTime());
  }

  // 모든 이미지 파일 조회
  async getAllImageFiles(): Promise<S3FileInfo[]> {
    const files = await this.listFiles();
    return files
      .filter((file) => file.type === "image")
      .sort((a, b) => b.lastModified.getTime() - a.lastModified.getTime());
  }

  // 특정 멤버의 데이터 조회
  async getMemberData(memberId: string): Promise<S3FileInfo[]> {
    const files = await this.listFiles();
    return files.filter((file) => file.memberId === memberId);
  }

  // 특정 게임의 데이터 조회
  async getGameData(gameId: string): Promise<S3FileInfo[]> {
    const files = await this.listFiles();
    return files.filter((file) => file.gameId === gameId);
  }

  // 감정별 파일 조회
  async getFilesByEmotion(emotion?: string): Promise<S3FileInfo[]> {
    const files = await this.listFiles();

    if (!emotion || emotion === "all") {
      return files.sort(
        (a, b) => b.lastModified.getTime() - a.lastModified.getTime()
      );
    }

    return files
      .filter((file) => file.emotion === emotion)
      .sort((a, b) => b.lastModified.getTime() - a.lastModified.getTime());
  }

  // 모든 감정 목록 가져오기
  async getEmotionList(): Promise<string[]> {
    const files = await this.listFiles();
    const emotions = new Set<string>();

    files.forEach((file) => {
      if (file.emotion) {
        emotions.add(file.emotion);
      }
    });

    return Array.from(emotions).sort();
  }

  // 감정별 통계
  async getEmotionStats(): Promise<
    Record<string, { count: number; imageCount: number; videoCount: number }>
  > {
    const files = await this.listFiles();
    const stats: Record<
      string,
      { count: number; imageCount: number; videoCount: number }
    > = {};

    files.forEach((file) => {
      if (file.emotion) {
        if (!stats[file.emotion]) {
          stats[file.emotion] = { count: 0, imageCount: 0, videoCount: 0 };
        }

        stats[file.emotion].count++;
        if (file.type === "image") {
          stats[file.emotion].imageCount++;
        } else {
          stats[file.emotion].videoCount++;
        }
      }
    });

    return stats;
  }

  // ArrayBuffer를 Base64로 변환 (큰 파일 안전 처리)
  private async arrayBufferToBase64(buffer: ArrayBuffer): Promise<string> {
    return new Promise((resolve, reject) => {
      try {
        const blob = new Blob([buffer]);
        const reader = new FileReader();

        reader.onload = () => {
          if (typeof reader.result === "string") {
            // data:application/octet-stream;base64, 부분 제거
            const base64 = reader.result.split(",")[1];
            resolve(base64);
          } else {
            reject(new Error("FileReader result is not a string"));
          }
        };

        reader.onerror = () => {
          reject(new Error("FileReader error"));
        };

        reader.readAsDataURL(blob);
      } catch (error) {
        reject(error);
      }
    });
  }

  // 파일 크기 포맷팅
  formatFileSize(bytes: number): string {
    if (bytes === 0) return "0 Bytes";

    const k = 1024;
    const sizes = ["Bytes", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
  }
}

export const s3AnalyticsService = new S3AnalyticsService();
