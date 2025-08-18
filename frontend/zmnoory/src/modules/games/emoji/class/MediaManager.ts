// [FILEPATH] src/modules/emojigame/class/MediaManager.ts
import type { Ref } from "vue";
import type { EmojiImageInfo, EmojiState } from "@/modules/games/emoji/types/emojiTypes";
import type { WebGLRenderer } from "three";

export class MediaManager{

    stream! : MediaStream
    _video! : HTMLVideoElement
    renderer : WebGLRenderer

    width : number = 500
    height : number = 400

    // 🎥 캔버스 (merge용)
    canvasMediaRecorder! : MediaRecorder
    normalMediaRecorder! : MediaRecorder
    mergeCanvas :HTMLCanvasElement
    mergeCtx : CanvasRenderingContext2D

    canvasRecordedChunks :Blob[] = []
    normalRecordedChunks : Blob[] = []

    constructor(width : number, height :  number, renderer : WebGLRenderer, video : HTMLVideoElement){
        this.width = width;
        this.height = height;
        this._video = video;
        this.renderer = renderer;
        this.mergeCanvas = document.createElement('canvas')
        this.mergeCanvas.width = this.width
        this.mergeCanvas.height = this.height
        this.mergeCtx = this.mergeCanvas.getContext('2d')!
        
    }

    async initializeMedia(){
        this.stream = await navigator.mediaDevices.getUserMedia({
            video: { width: this.width, height: this.height },  //790
            audio: true,
            }
        )
    }

    update(){
        if (this._video) 
        this.mergeCtx.drawImage(this._video, 0, 0, this.mergeCanvas.width, this.mergeCanvas.height)
        this.mergeCtx.drawImage(this.renderer.domElement, 0, 0, this.mergeCanvas.width, this.mergeCanvas.height)
    }

    stop(){
        this.stopRecording();
        this.dispose();
        if (this._video) {
            this._video.srcObject = null;
            this._video.pause();
            this._video.src = "";
        }
    }

    dispose(){
        if (this.stream) {
            this.stream.getTracks().forEach(track => track.stop())
        }
    }


    cropImage(temp_state_emoji : EmojiState, repeatCount : number) : EmojiImageInfo{
        let video : HTMLVideoElement = this._video;
        
        if(video == null){
            console.log("멸망");
        }

        // console.log( video.videoHeight + " " + video.videoWidth)
        const temp_canvas = document.createElement('canvas')
        temp_canvas.height = video.videoHeight
        temp_canvas.width = video.videoWidth

        const ctx = temp_canvas.getContext('2d')!
        if(!ctx){
            console.error("2d context가 없다 아이가")
            return {
            image : '',
            imageName : ''
            }
        }
    
        //console.log(temp_state_emoji.emojiStr  + " 캡처 완료!!")
        ctx.drawImage(video, 0,0, temp_canvas.width, temp_canvas.height)
        const base64Data = temp_canvas.toDataURL('image/png');

        // console.log("이미지 ", base64Data)

        // const blob = this.base64ToBlob(base64Data); 
        // const bytes = blob.size;
        // const kb = (bytes / 1024).toFixed(2);   // KB로 변환 → 약 375.08 KB
        // const mb = (bytes / 1024 / 1024).toFixed(2);  // MB로 변환 → 약 0.37 MB
        // console.log("🎯 정확한 Blob 크기:", blob.size, "bytes ", kb, "kb ",mb,"mb ");

        return {
            image :  base64Data,
            imageName : temp_state_emoji.emojiStr + repeatCount
        }
    }

    base64ToBlob(base64Data: string, contentType = 'image/png') : Blob {
        const byteCharacters = atob(base64Data.split(',')[1]);
        const byteArrays = [];

        for (let i = 0; i < byteCharacters.length; i += 512) {
            const slice = byteCharacters.slice(i, i + 512);

            const byteNumbers = new Array(slice.length);
            for (let j = 0; j < slice.length; j++) {
                byteNumbers[j] = slice.charCodeAt(j);
            }

            byteArrays.push(new Uint8Array(byteNumbers));
        }

        return new Blob(byteArrays, { type: contentType });
    }



    /**
     * 게임을 녹화합니다.
     * 
     * @param frameRate 몇 프레임으로 녹화할 지
     * @param ref_recordedVideoURL 실제 녹화된 비디오가 저장될 Ref객체
     */
    startRecording(frameRate : number, ref_recordedVideoURL : Ref, ref_normalRecordedVideoURL : Ref) {
        this.canvasRecordedChunks = []
        this.normalRecordedChunks = []

        //mergeCanvas는 Three.js 가 그려진 캔버스
        const videoStream = this.mergeCanvas.captureStream(frameRate) // FPS 설정
        const audioTracks = this.stream?.getAudioTracks() || []
        for(const track of audioTracks){
            videoStream.addTrack(track)
        }

        const normalStream = this.stream.clone();

        this.canvasMediaRecorder = new MediaRecorder(videoStream, { mimeType: 'video/webm' })
        this.normalMediaRecorder =  new MediaRecorder(normalStream ,{mimeType : 'video/webm'})


        this.canvasMediaRecorder.ondataavailable = (e) => {
        if (e.data.size > 0) this.canvasRecordedChunks.push(e.data)
        }

        this.normalMediaRecorder.ondataavailable = (e) => {
            if (e.data.size > 0) this.normalRecordedChunks.push(e.data)
        }

        this.canvasMediaRecorder.onstop = () => {
            const blob = new Blob(this.canvasRecordedChunks, { type: 'video/webm' })
            ref_recordedVideoURL.value = URL.createObjectURL(blob)
        }

        this.normalMediaRecorder.onstop = () => {
            const blob = new Blob(this.normalRecordedChunks, { type: 'video/webm' })
            ref_normalRecordedVideoURL.value = URL.createObjectURL(blob)
        }

        this.canvasMediaRecorder.start()
        this.normalMediaRecorder.start()
    }




    stopRecording(){
        if (this.canvasMediaRecorder && this.canvasMediaRecorder.state === 'recording') {
            this.canvasMediaRecorder.stop()
        }
        if (this.normalMediaRecorder && this.normalMediaRecorder.state === 'recording') {
            this.normalMediaRecorder.stop()
        }
    }


}