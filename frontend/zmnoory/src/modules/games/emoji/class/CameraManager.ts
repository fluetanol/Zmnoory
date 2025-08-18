// [FILEPATH] src/modules/emojigame/class/CameraManager.ts
import type {  Mesh, PerspectiveCamera } from "three";
import {  Vector3 } from "three";
import { LifeCycle } from "./LifeCycle";


export class CameraManager extends LifeCycle {
    
    _camera :  PerspectiveCamera
    private _followTarget? : Mesh
    private _isFollow : boolean = false;
    private _isLookAt : boolean = false;
    private _followOffset : Vector3;
    private _lookOffset : Vector3;
    private _followDistance : number = 15

    //private _clock  :Clock;

    constructor(camera : PerspectiveCamera, followDistance : number){
        super();
        this._camera = camera
        this._followOffset = new Vector3(0,0,0);
        this._lookOffset =  new Vector3(0,0,0);
        this._followDistance = followDistance
        //this._clock = new Clock();
    }


    override update() : void{
        if(this._isFollow){
            this.following();
        }
    }

    public set followTarget(target:Mesh){
        this._followTarget = target
    }

    public set followOffset(offset : Vector3){
        this._followOffset = offset;
    }

    public set lookOffset(offset :  Vector3){
        this._lookOffset = offset;
    }

    public set isLookAt(isLookAt : boolean){
        this._isLookAt = isLookAt;
    }

    public following(){
        const followPosition = this._followTarget!.position;
        const cameraPosition = this._camera.position;
        
        cameraPosition.x =  followPosition.x + this._followOffset.x;
        cameraPosition.y =  followPosition.y + this._followOffset.y;
        cameraPosition.z  = followPosition.z + this._followOffset.z;

        if(this._isLookAt){
            const lookPosition = this._followTarget!.position.clone();
            lookPosition.x += this._lookOffset.x;
            this._camera.lookAt(lookPosition);
        }

        //console.log("time : " + GameUtils.deltaTime);
    }

    public initializeCamera(target_agent :Mesh){
        console.log("set caemra " ,target_agent)
        this.isLookAt = true;
        this._isFollow = true;
        this.followTarget = target_agent;
        this.followOffset = new Vector3(3, 5, this._followDistance)
        this.lookOffset =  new Vector3(-10, 0, 0);
        this._camera.position.z = target_agent.position.z
    }
    
    

}