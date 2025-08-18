import {
  BoxGeometry,
  BufferGeometry,
  CircleGeometry,
  CylinderGeometry,
  DirectionalLight,
  Group,
  Material,
  MathUtils,
  Mesh,
  MeshStandardMaterial,
  PerspectiveCamera,
  Scene,
  Texture,
  TextureLoader,
  Vector2,
  WebGLRenderer} from "three"


import { EmojiManager } from "./EmojiManager"
import { GameUtils } from "./GameUtils"
import { EffectComposer, RenderPass, UnrealBloomPass } from "three/examples/jsm/Addons.js"
import type { EmojiState, ObjectInfo, State_Current } from "@/modules/games/emoji/types/emojiTypes"

export class SceneManager {
  scene: Scene
  renderer: WebGLRenderer
  camera: PerspectiveCamera
  composer: EffectComposer

  scenewidth: number
  sceneheight: number

  obj_agent!: Mesh
  obj_land!: Mesh
  obj_finishline!: Mesh
  obj_finishland!: Mesh
  group_finish!: Group

  list_obj_wall: Mesh[] = []
  list_obj_emojiface: Mesh[] = []
  list_obj_group_wall: Group[] = []
  list_obj_wallinfo: ObjectInfo[] = []

  wallCount: number

  // Textures
  tex_emoji!: Texture
  tex_wood!: Texture
  tex_road!: Texture
  tex_normal_road!: Texture
  tex_finishline!: Texture
  tex_wall!: Texture
  tex_normal_wall!:Texture

  // Geometries
  geo_land!:BoxGeometry
  geo_agent!: CylinderGeometry
  geo_wall!: BoxGeometry
  geo_emojiFace!: CircleGeometry
  geo_finishline!: BoxGeometry
  geo_finishland!: BoxGeometry

  // Materials
  mat_wall!: MeshStandardMaterial
  mat_agent!: MeshStandardMaterial
  mat_emoji_list: MeshStandardMaterial[] = []
  mat_wood!: MeshStandardMaterial
  mat_finishline!: MeshStandardMaterial
  mat_road!: MeshStandardMaterial

  constructor(canvas : HTMLCanvasElement, wallCount: number, width: number, height: number) {
        //카메라 셋팅
    this.camera = new PerspectiveCamera(60, width / height, 0.1, 1000)
    this.scene = new Scene()
    this.renderer = new WebGLRenderer({ canvas, alpha: true }) // alpha: true → 배경 투명
    this.scenewidth = width
    this.sceneheight = height
    this.wallCount = wallCount
    this.renderer.shadowMap.enabled = true

    this.composer = new EffectComposer(this.renderer)
    this.initializeObject()
  }

  update(){
    this.renderer.render(this.scene, this.camera)
  }

  stop(){
    this.dispose()
    this.composer.dispose()
    this.renderer.dispose()
  }

  initializeObject() {
    const renderPass = new RenderPass(this.scene, this.camera)
    this.composer.addPass(renderPass)
    const bloomPass = new UnrealBloomPass(new Vector2(this.scenewidth, this.sceneheight), 1.5, 0.4, 0.85)
    this.composer.addPass(bloomPass)
    
    // Load Textures
    this.initializeTextures();
    this.initializeGeometries();
    this.initializeMaterials();
    this.initializeObjects();
    this.initializeGroup();

    for (let i = 0; i < this.wallCount; ++i) {
      this.list_obj_wallinfo.push({
        object: this.list_obj_wall[i],
        velocity: GameUtils.forward,
        size: this.list_obj_wall[i].scale,
        scale: this.list_obj_wall[i].scale,
        isActive: true
      })
    }

    this.initializeSceneAdd()
    this.initializeSceneObjectPosition()
    this.initializeLight()
  }

  private initializeTextures(){
    this.tex_emoji = new TextureLoader().load('/textures/tex_emoji.png')
    this.tex_wood = new TextureLoader().load('/textures/tex_wood.jpg')
    this.tex_road = new TextureLoader().load('/textures/tex_road.png')
    this.tex_normal_road = new TextureLoader().load('/textures/tex_normal_road.png')
    this.tex_finishline = new TextureLoader().load('/textures/tex_finishline.jpg')
    this.tex_wall = new TextureLoader().load('/textures/tex_wall.png')
    this.tex_normal_wall = new TextureLoader().load('/textures/tex_normal_wall.png')
  }

  private initializeGeometries() {
    this.geo_land = new BoxGeometry(6, 0.1, 40)
    this.geo_agent = new CylinderGeometry(1.5, 1.5, 1, 32)
    this.geo_wall = new BoxGeometry(6, 10, 2)
    this.geo_emojiFace = new CircleGeometry(1, 32)
    this.geo_finishline = new BoxGeometry(10, 0.1, 2)
    this.geo_finishland = new BoxGeometry(10, 0.2, 20)
  }

  private initializeMaterials() {
// Create Materials
    this.mat_wall = new MeshStandardMaterial({ map: this.tex_wall, normalMap: this.tex_normal_wall, roughness: 1.0 })
    this.mat_agent = new MeshStandardMaterial({ map: this.tex_emoji })
    const baseEmojiMat = new MeshStandardMaterial({ map: EmojiManager.list_tex_emoji[0], transparent: true })
    this.mat_emoji_list.push(baseEmojiMat)
    for (let i = 1; i < this.wallCount; ++i) {
      this.mat_emoji_list.push(baseEmojiMat.clone())
    }

    this.mat_wood = new MeshStandardMaterial({ map: this.tex_wood })
    this.mat_finishline = new MeshStandardMaterial({ map: this.tex_finishline })
    this.mat_road = new MeshStandardMaterial({ map: this.tex_road, normalMap: this.tex_normal_road })
  }

  private initializeObjects(){
    // Create Meshes
    this.obj_agent = new Mesh(this.geo_agent, this.mat_agent)
    const poss = this.obj_agent.position.clone()
    poss.y = 10
    this.obj_agent.lookAt(poss)

    this.obj_land = new Mesh(this.geo_land, this.mat_road)
    this.list_obj_wall.push(new Mesh(this.geo_wall, this.mat_wall))
    this.list_obj_emojiface.push(new Mesh(this.geo_emojiFace, this.mat_emoji_list[0]))

    for (let i = 1; i < this.wallCount; ++i) {
      this.list_obj_wall.push(this.list_obj_wall[0].clone())
      const emojiFace = this.list_obj_emojiface[0].clone()
      emojiFace.material = this.mat_emoji_list[i]
      this.list_obj_emojiface.push(emojiFace)
    }

    this.obj_finishline = new Mesh(this.geo_finishline, this.mat_finishline)
    this.obj_finishland = new Mesh(this.geo_finishland, this.mat_wood)
  }

  private initializeGroup(){
    for (let i = 0; i < this.wallCount; ++i) {
      const group_wall = new Group()
      group_wall.add(this.list_obj_wall[i])
      group_wall.add(this.list_obj_emojiface[i])
      this.list_obj_group_wall.push(group_wall)
    }
  
    this.group_finish = new Group()
    this.group_finish.add(this.obj_finishland)
    this.group_finish.add(this.obj_finishline)

  }

  initializeSceneObjectPosition() {
    this.obj_agent.position.z = 5
    this.obj_agent.rotation.y = MathUtils.degToRad(90)
    this.obj_land.position.y = -5
    console.log("에이전트 위치 초기화 ",this.obj_agent.position.z, + " " +this.obj_agent.id);

    let zposition = 0
    for (let i = 0; i < this.wallCount; ++i) {
      this.list_obj_wall[i].position.z = zposition
      zposition -= 10
    }

    for (let i = 0; i < this.wallCount; ++i) {
      this.list_obj_emojiface[i].position.y = 4
      this.list_obj_emojiface[i].position.z = this.list_obj_wall[i].position.z + 2
    }

    this.group_finish.position.y = 100
    this.group_finish.position.z = 100
  }

  private initializeSceneAdd() {
    this.scene.add(this.obj_agent)
    this.scene.add(this.obj_land)
    this.scene.add(this.group_finish)
    for (let i = 0; i < this.wallCount; ++i) {
      this.scene.add(this.list_obj_group_wall[i])
    }
  }

  initializeLight() {
    const dlight = new DirectionalLight(0xffffff, 4)
    dlight.position.set(0, 10, 10)
    this.scene.add(dlight)
  }


  makeEmojiStateCurrentList(list_obj_emoji_state :EmojiState[]) : State_Current[]{
      const list_States :State_Current[] = [];
      for(let i=0; i<this.wallCount; ++i){
      list_States.push({
        current_group : this.list_obj_group_wall[i] ,
        current_info : this.list_obj_wallinfo[i],
        current_state_emoji: list_obj_emoji_state[i],
        current_wall:this.list_obj_wall[i]
      })
    }
      return list_States!
  }
  

  dispose() {
    const disposeMesh = (mesh: Mesh) => {
      mesh.geometry?.dispose()
      if (Array.isArray(mesh.material)) {
        mesh.material.forEach(mat => mat?.dispose())
      } else {
        mesh.material?.dispose()
      }
      this.scene.remove(mesh)
    }

    const disposeTexture = (tex?: Texture) => tex?.dispose()
    const disposeMaterial = (mat?: Material) => mat?.dispose()
    const disposeGeometry = (geo?: BufferGeometry) => geo?.dispose()

    disposeMesh(this.obj_agent)
    disposeMesh(this.obj_land)
    disposeMesh(this.obj_finishline)
    disposeMesh(this.obj_finishland)

    this.list_obj_wall.forEach(disposeMesh)
    this.list_obj_emojiface.forEach(disposeMesh)
    this.list_obj_group_wall.forEach(group => this.scene.remove(group))
    this.scene.remove(this.group_finish)

    disposeTexture(this.tex_emoji)
    disposeTexture(this.tex_wood)
    disposeTexture(this.tex_road)
    disposeTexture(this.tex_normal_road)
    disposeTexture(this.tex_finishline)
    disposeTexture(this.tex_wall)
    disposeTexture(this.tex_normal_wall)

    disposeGeometry(this.geo_land)
    disposeGeometry(this.geo_agent)
    disposeGeometry(this.geo_wall)
    disposeGeometry(this.geo_emojiFace)
    disposeGeometry(this.geo_finishline)
    disposeGeometry(this.geo_finishland)

    disposeMaterial(this.mat_wall)
    disposeMaterial(this.mat_agent)
    disposeMaterial(this.mat_wood)
    disposeMaterial(this.mat_finishline)
    disposeMaterial(this.mat_road)
    this.mat_emoji_list.forEach(disposeMaterial)

    for(let i = 0; i<this.composer.passes.length; ++i){
      this.composer.passes[i].dispose();
    }
    this.composer.passes = []
    
    this.renderer.dispose()
  }
}
