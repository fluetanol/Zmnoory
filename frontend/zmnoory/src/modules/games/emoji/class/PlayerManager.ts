// [FILEPATH] src/modules/emojigame/class/PlayerManager.ts
import { Box3, type Mesh, type Vector3 } from "three";
import { LifeCycle } from "./LifeCycle";
import { GameUtils } from "./GameUtils";

export class PlayerManager extends LifeCycle{
    
     _obj_player : Mesh
    //넉백 중인지 (넉백 가능 여부가 아님)
    private _is_knockback : boolean = false;
    //넉백 시간
    private _knockbackTime : number = 0;
    //넉백 진행 시간
    private _time_elapsed_knockback : number = 0;
    //넉백 속도
    private _speed_knockback : number = 0;
    //이속
    private _speed : number = 0;
    //플레이어의 forward vector
    private _forward : Vector3 = GameUtils.forward;
    //현재 플레이어의 이모지 상태
    state_emoji : number = -1;


    constructor(obj_player:Mesh){
        super();
        this._obj_player = obj_player
    }

    public set speed(speed : number){
        this._speed = speed;
    }

    public set knockbackTime(knockbackTime :number){
        this._knockbackTime = knockbackTime;
    }

    public get isKnockback() : boolean {
        return this._is_knockback
    }

    public set isKnockback(isKnockback :boolean){
        this._is_knockback = isKnockback;
    }

    public set knockbackSpeed(knockbackSpeed : number){
        this._speed_knockback = knockbackSpeed
    }


    public set forward(forward:Vector3){
        this._forward = forward
    } 

     override update(): void {
        if(this._is_knockback){
            this.knockback();
        }
        else{
            this.moveAgent();
        }       
     }

     initializePlayer(){
        this.speed = -11
        this.knockbackTime = 0.8;
        this.knockbackSpeed = 6;
    
     }

    moveAgent() : void{
       const vec_forward = this._forward.clone();
       //console.log("delta 값 ",GameUtils.deltaTime);

       if(GameUtils.deltaTime > 0.03){
        console.log("비정상적인 deltaTIme", GameUtils.deltaTime);
       }
       this._obj_player.position.add(vec_forward.multiplyScalar(this._speed * GameUtils.deltaTime));
       //console.log("저는 이동중입니다 ", this._obj_player.id + " ",this._obj_player.position);
       //  // obj_agent.position.z-= agentSpeed * clock_delta;
    }

    stop() :void{
        this._speed = 0;
    }

    knockback() : void{
        this._time_elapsed_knockback+= GameUtils.deltaTime;
         //console.log("knockback : " + this._time_elapsed_knockback + " " + this._knockbackTime)

        if(this._time_elapsed_knockback < this._knockbackTime){
            const vec_forward = this._forward.clone();
            this._obj_player.position.add(vec_forward.multiplyScalar(this._speed_knockback * GameUtils.deltaTime));
        }   
        else{
            this._time_elapsed_knockback = 0;
            this._is_knockback = false;
        }
    }

    isCollision(box_other:Box3):boolean{
        const box_agent = new Box3().setFromObject(this._obj_player)
        //console.log("iScOLLISION!")
        if(!this._is_knockback && box_agent.intersectsBox(box_other)){
            return true;
        }

        else
            return false;
    }


}