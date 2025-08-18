import {  type Ref } from "vue";
import { MediaUtils } from "./MediaUtils";
import type { VideoClipInfo } from "../types/FamouslineTypes";

export class MediaManager{

    stream : MediaStream

    ref_video:Ref<HTMLVideoElement | null>
    ref_maincanvas:Ref<HTMLCanvasElement | null>;
    ref_recordedVideoURL:Ref<string | null> ;


    /**
     * 클립 비디오에 대한 참조
     */
    ref_refClip:Ref<HTMLVideoElement | null>;
    /**
     * 클립 비디오를 재생할 지 (보여줄 지) 에 대한 여부
     */
    ref_isShowClip:Ref<boolean>;


    REF_CLIP_SRC : string[] = ['/clips/clip_takeEverything.mp4'];


    Info_VideoClips : VideoClipInfo[] = [
    {
        src: '/clips/clip_takeEverything.mp4',
        line: '꼭 그렇게 다 가져가야만 속이 후련했냐!'
    }
    ]

    constructor(stream : MediaStream,  ref_video: Ref<HTMLVideoElement | null>, ref_maincanvas: Ref<HTMLCanvasElement | null>, ref_recordedVideoURL: Ref<string | null>,  ref_refClip: Ref<HTMLVideoElement | null>, ref_isShowClip: Ref<boolean>) {
        this.stream = stream;
        this.ref_video = ref_video;
        this.ref_maincanvas = ref_maincanvas;
        this.ref_recordedVideoURL = ref_recordedVideoURL;
        this.ref_refClip = ref_refClip;
        this.ref_isShowClip = ref_isShowClip;
    }


    async start(){
        await MediaUtils.playVideoStream(this.ref_refClip.value, false);
        this.ref_refClip.value!.onended = () => {
            console.log("클립 재생 완료");
            this.ref_isShowClip.value = false;
        }
    }


    update(){

    }


    destroy(){
        this.ref_video.value = null;
        this.ref_maincanvas.value = null;
        this.ref_recordedVideoURL.value = null;
        this.ref_refClip.value = null;
        this.ref_isShowClip.value = false;
        MediaUtils.disposeStream(this.stream);
    }

    addClip(clipStr : string){
        this.REF_CLIP_SRC.push(clipStr);
    }

    choiceRandomClip() : string{
        const randomIndex = Math.floor(Math.random() * this.REF_CLIP_SRC.length);
        return this.REF_CLIP_SRC[randomIndex];
    }

}