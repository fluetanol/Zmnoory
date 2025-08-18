import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from '@/services/axios'

export const useS3UploadStore = defineStore('upload', () => {
  
    const uploadVideoResult = ref<boolean|null>(null);

    const uploadVideoToS3 = async(blob:Blob, presigned_url:string) : Promise<void> => {
        console.log(presigned_url , + " ", blob)
        try{
            const res = await axios.put(presigned_url, blob, {
                    headers:{
                        'Content-Type' :'video/webm',
                        }
                    })
                 uploadVideoResult.value = true;
                 console.log("s3 진짜 업로드 성공", res)
             }   
            catch(error)
            {
                uploadVideoResult.value =false;
                console.error('S3 업로드 실패', error);
            }
        }

    

  
    return {
        uploadVideoResult,
        uploadVideoToS3
    }

})