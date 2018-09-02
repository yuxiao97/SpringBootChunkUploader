package com.yuxiao.springboot.chunkuploader;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * @Auther: Yuxiao
 * @Date: 2018/6/24 22:29
 * @Description:
 */
@Controller
public class UploadController {

    @RequestMapping({"/","upload.html"})
    public ModelAndView showUploadPage(){
        return new ModelAndView("upload");
    }


    /**
     * 文件上传请求
     * @param request
     * @param multipartFileParam
     * @param model
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public void upload(HttpServletRequest request, MultipartFileParam multipartFileParam, Model model) throws IOException{
        String rootPath = "D:/";
        boolean finished = MultipartFileDealUtil.uploadFileRandomAccessFile(multipartFileParam, rootPath);
        if(finished){
            // 文件上传完成后，进行其他的业务逻辑处理(比如文件信息如果或权限信息关联等一系列操作)
            // 如果上传文件的位置不是文件的最终保存位置，可以将文件移动到目标位置后清除临时文件和临时文件的配置文件.conf
            System.out.println("upload finished.");
            //MultipartFileDealUtil.cleanTempFile(multipartFileParam, rootPath);
        }
    }

}
