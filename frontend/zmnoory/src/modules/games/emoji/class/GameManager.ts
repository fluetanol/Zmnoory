// [FILEPATH] src/modules/emojigame/class/GameManager.ts
import type { GameData, ObjectInfo, State_Current, Type_FormatTime } from "@/modules/games/emoji/types/emojiTypes";
import { BoxGeometry, Group, Mesh, Vector3 } from "three";
import type { SceneManager } from "./SceneManager";
import { GameUtils } from "./GameUtils";
import type { Ref } from "vue";

//게임 규칙 관리
export class GameManager{

    private _sceneManager: SceneManager

    //지면 오브젝트
    _land_obj : Mesh
    
    //각 벽이 가지고 있는 상태 (매핑 된 이모지 정보, 오브젝트 위치 정보 등)
    _list_States : State_Current[] = [];
    //현재 에이전트가 몇 번째 벽과 부딪혀서 비교 평가해야하는지?
    _wallidx = 0;

    /**  
        현재 게임 상태를 종합적으로 볼 수 있음
    */
    state_current! : State_Current

    /**
     * 이모지 게임의 벽이 몇번 반복되는지 
     */
    repeatCount: number = 0
    /**
     * 게임이 끝났는지 여부 (승리한 케이스)
     * @type {boolean}
     */
    gameOver : boolean  = false;
    /**
     * 게임이 끝났는지 여부 (시간 초과로 인한 패배 케이스)
     * @type {boolean}
     */
    timeOver : boolean = false;
    /**
     * 게임이 끝났는지 여부 (게임이 끝났을 때, 화면에 표시되는 메시지 출력 여부)
     * @type {boolean}
     */
    finishCheck  : boolean = false;

    /**
     * 게임 오버 시간
     */
    overTime = 90

    overtick = 0
    overId = 0
    overTickFrame = 120

    gameStart = false

    callbackGameOver: (() => void) | null = null;
    callbackTimeOver: (() => void) | null = null;   

    typeTime! :Type_FormatTime
    ref_timeOver: Ref<boolean>
    
    gameData: GameData = {
        mapRepeat: 3,
        knockBackDuration: 40,
        detectTick: 40,
    }

    constructor(land_obj:Mesh, list_States:State_Current[], sceneManager: SceneManager, ref_timeOver: Ref<boolean>){
        this._land_obj = land_obj;
        this._list_States = list_States
        this.state_current = this._list_States[0];
        this._sceneManager = sceneManager;
        this.ref_timeOver = ref_timeOver;
    }

    
    update(){
        if(this.checkgameOver(this._sceneManager)){
            this.callbackGameOver?.();
        }

        if(GameManager.checkTimeOver(this.overTime, this.ref_timeOver.value)){
            this.callbackTimeOver?.();
        }
    }
    
    static checkTimeOver(overTime:number, timeOver:boolean) : boolean{
        return GameUtils.remainTime(overTime) <= 1 && !timeOver;
    }

    //현재 맞춰야 할 이모지 상태를 갱신합니다.
    changeCurrentState(){
        const size = this._list_States.length;
        this.state_current = this._list_States[++this._wallidx % size];
    }

    //벽 오브젝트를 일정 간격 만큼 이동시킵니다. (게임 특성상 지면도 이동함)
    static offsetWall(wall : Group , land_obj : Mesh, offset:number){
        wall.position.z = wall.position.z - 20;
        land_obj.position.z -= offset/2
    }

    //오브젝트 info를 생성합니다.
    makeObjectInfo(obj : Mesh) : ObjectInfo{
        const geo_tempwall = obj.geometry as BoxGeometry
        const { width, height, depth }  = geo_tempwall.parameters
        const size_tempwall =  new Vector3(width, height, depth)

        return {
            object : obj,
            velocity : new Vector3(0,0,0),
            size : size_tempwall,
            scale : obj.scale.clone(),
            isActive : true,
        }
    }

    checkgameOver(sceneManager: SceneManager) :boolean{
        if(!this.state_current.current_info.isActive){
            if(!this.finishCheck){
             sceneManager.group_finish.position.z = sceneManager.obj_land.position.z - 20
             sceneManager.group_finish.position.y = -4.5
             sceneManager.obj_finishline.position.z = sceneManager.obj_finishland.position.z + 5
             sceneManager.obj_finishline.position.y = sceneManager.obj_finishland.position.y + 0.125
            this.finishCheck = true;
        }

        if(sceneManager.obj_agent.position.z < sceneManager.group_finish.position.z && !this.gameOver){
                    console.log("end the game")
                
                    // StopRecording();
                    this.gameOver = true    
                    //return;
                }
        }
        else if (this.gameOver){
            return false;
        }


        return this.gameOver
    }    
         
    




}