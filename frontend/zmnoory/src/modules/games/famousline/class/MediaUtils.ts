import type { Ref } from "vue";


export class MediaUtils{

    /**
     * 비디오로부터 미디어 스트림을 얻습니다.
     * 
     * @param constraints MediaStreamConstraints 객체
     * @param ref_video HTMLVideoElement Ref 객체
     * @returns 
     */
    static async getUserMediastream(constraints: MediaStreamConstraints, video?: HTMLVideoElement | null): Promise<MediaStream> {
        try {
        const stream = await navigator.mediaDevices.getUserMedia(constraints);
        if (video) {
            video.srcObject = stream;
        }
        return stream;
        } catch (error) {
        console.error('Error accessing media devices.', error);
        throw error;
        }
    }
    
    static disposeStream(stream: MediaStream): void {
        stream.getTracks().forEach(track => track.stop());
    }

    static startRecord(stream: MediaStream,  recordFinishBlobRef: Ref<Blob | null>,  timeSlice? : number, mimeType : string = 'video/webm') : MediaRecorder{
        const recordedChunks :Blob[] = []

        const recorder = new MediaRecorder(stream, {
            mimeType: mimeType
        });
        
        recorder.ondataavailable = (e) => {
        if (e.data.size > 0) recordedChunks.push(e.data)
        }

        recorder.onstop = () => {
            recordFinishBlobRef.value = new Blob(recordedChunks, { type: mimeType })
        }

        recorder.start(timeSlice);
        return recorder;
    }

    static async finishRecord(recorder: MediaRecorder): Promise<void> {
        try{ recorder.requestData(); } catch{}

        await new Promise<void>((resolve) => {
            const onStop = () =>{
                recorder.removeEventListener('stop', onStop);
                resolve();
            }
            recorder.addEventListener('stop', onStop);
            recorder.stop();
        });
    }

    static connectBase64URL(ref_recordedVideoURL: Ref<string | null>, ref_recordVideoBlob: Ref<Blob | null>){
        if (ref_recordedVideoURL.value) URL.revokeObjectURL(ref_recordedVideoURL.value);
        ref_recordedVideoURL.value = URL.createObjectURL(ref_recordVideoBlob.value!)
    }

    static async playVideoStream(clip : HTMLVideoElement | null, showControl? :boolean){
        try{
            if(!clip) return;
            clip.load();
            clip.currentTime = 0;
            clip.controls = showControl ? showControl : false;
            clip.disablePictureInPicture = false;
            console.log("play!");
            await clip.play();
        }
        catch(e){
            console.error("클립 재생 실패:", e);
        }
    }

    static stopVideoStream(ref_refClip : Ref<HTMLVideoElement | null>, stopAction : () => void) {
        ref_refClip.value!.onended = () => {
            stopAction();   
        }
    }
}