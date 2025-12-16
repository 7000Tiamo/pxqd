package com.training.controller;



import com.training.util.QrCodeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QrCodeController {

    // 从配置文件读取前端域名（推荐）
    @Value("${app.frontend.domain:http://172.25.8.77:3000}")
    private String frontendDomain;

    /**
     * 生成签到二维码
     * 示例请求: GET /api/qrcode?trainingId=5
     * 返回: PNG 图片
     */
    @GetMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateCheckinQrCode(@RequestParam Long trainingId) {
        try {
            // 构造签到页面 URL
            String checkinUrl = frontendDomain + "/checkin?training_id=" + trainingId;

            // 生成二维码（300x300 像素）
            byte[] qrCodeImage = QrCodeUtil.generateQrCode(checkinUrl, 300, 300);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);

            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
