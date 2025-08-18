// [FILEPATH] src/modules/emojigame/class/EmojiManager.ts
import { TextureLoader, type Mesh, type Texture } from "three";
import { GameUtils } from "./GameUtils";
import type { FaceExpressions } from "face-api.js";
import type { EmojiState, State_Current } from "@/modules/games/emoji/types/emojiTypes";


export class EmojiManager{

    /**
     * emoji state를 정의합니다. (0~6)
     * 
     * - 0 : angry
     * - 1 : disgusted
     * - 2 : fearful
     * - 3 : happy
     * - 4 : neutral
     * - 5 : sad
     * - 6 : surprised
     */
    static list_emoji_state: number[] = [0, 1, 2, 3, 4, 5, 6]

    /**emoji state에 대응되는 text를 반환해줍니다.
     */
    static map_state_name = new Map<number, string>([
        [0, 'angry'],
        [1, 'disgusted'],
        [2, 'fearful'],
        [3, 'happy'],
        [4, 'neutral'],
        [5, 'sad'],
        [6, 'surprised'],
    ])

    //실제 이모지 아틀라스 파일을 기준으로, 각 이모지 그림이 어떤 감정에 대응되는지에 대한 라벨을 정의합니다.
    static map_emoji_atlas_state = new Map<number, number>([
        [0, 0], [1, 1], [2, 2], [3, 3],
        [4, 4], [5, 5], [6, 6],
    ])

    static list_tex_emoji: Texture[] = []

    //각 이모지가 몇 번 통과되었는지 기록함
    //가령 지금 시스템에서 0번 배열이 ++되었다면 angry 표정을 한번 통과했다는 뜻
    list_emoji_count:number[] = [0,0,0,0,0,0,0]

    /** 
     벽에 붙어있는 이모지 오브젝트들의 상태 배열
    */
    list_obj_emoji : Mesh[] = []
    list_obj_emoji_state : EmojiState[] = []

    /**
     * 현재 이모지 오브젝트 상태
     */
    current_emotionState : number = -1
    current_emojiState! : EmojiState

    constructor(list_tex_emoji : Mesh[]){
        this.list_obj_emoji = list_tex_emoji

        const tex_emojiatlas =  new TextureLoader().load('/textures/tex_emoji_atlas.png');
        const cols = 7
        const rows = 1
        const baseWidth = 1 / cols
        const baseHeight = 1 / rows
        //10
        const margin = 0  // 좌우 여백 (UV 기준, 약 0.2%씩 안쪽에서 자름)
        this.initializeEmojiTexture(rows, cols, baseWidth, baseHeight, margin, tex_emojiatlas);

    }
    
    update(){}

    //이모지를 아틀라스에서 분할해서 초기화합니다.
    initializeEmojiTexture(rows:number, cols:number, baseWidth :number, baseHeight : number, margin: number, tex_emoji_atlas : Texture){
        for (let y = 0; y < rows; y++) {
            for (let x = 0; x < cols; x++) {
                const tex = tex_emoji_atlas.clone()

                tex.repeat.set(baseWidth - margin * 2, baseHeight)
                tex.offset.set(x * baseWidth + margin, 1 - (y + 1) * baseHeight)

                tex.needsUpdate = true
                EmojiManager.list_tex_emoji.push(tex)
            }
        }


        for(let i=0; i<this.list_obj_emoji.length; ++i){
            let state =  EmojiManager.randomizeEmojiTexture(this.list_obj_emoji[i]);
            this.list_obj_emoji_state.push(
                {
                    emojiObj : this.list_obj_emoji[i],
                    emojiobjID : this.list_obj_emoji[i].id,
                    emojistate : state,
                    emojiStr : EmojiManager.map_state_name.get(state)!
                }
            )
        }
        this.current_emojiState = this.list_obj_emoji_state[0];
    }


    //이모지 텍스처를 랜덤하게 하나 얻고, 이를 Mesh에 입힙니다.
   static randomizeEmojiTexture(obj_temp_emoji:Mesh) : number{
        let idx :number =  Math.floor(Math.random() *  EmojiManager.list_emoji_state.length)
        GameUtils.changeTexture(obj_temp_emoji, this.list_tex_emoji[idx]);

        let label = EmojiManager.list_emoji_state[idx];
       // console.log("random emoji! " + obj_temp_emoji.id + " " + "emojiidx : " + idx  + " label :  " + label + " " + EmojiManager.map_state_name.get(label));
        return label;
    }

    static changeEmojiTexture(current_state: State_Current, state: number){
            current_state.current_state_emoji.emojistate = state
            current_state.current_state_emoji.emojiStr = EmojiManager.map_state_name.get(state)!
    }

    //현재 가장 유력한 감정을 추출합니다.
   getCurrentEmotion(expressions : FaceExpressions, dirtycheck : boolean){
        let max = -1;
        let maxidx = -1;
        
        if(dirtycheck){
            const arr:number[] = [expressions.angry, 
                expressions.disgusted, 
                expressions.fearful, 
                expressions.happy,
                expressions.neutral, 
                expressions.sad, 
                expressions.surprised]
            for(let i=0; i<arr.length; i++){
            if(max<arr[i]){
                max = arr[i];
                maxidx = i;
            }
            }

       // console.log(EmojiManager.map_state_name.get(maxidx) + " " + max + " " + maxidx);
        this.current_emotionState = maxidx;
        dirtycheck  = false;
        }
        

        return maxidx;
    }





}