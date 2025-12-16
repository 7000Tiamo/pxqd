package com.training.controller;

import com.training.common.api.BizException;
import com.training.common.api.ErrorCode;
import com.training.entity.User;
import com.training.mapper.UserMapper;
import com.training.service.AuthService;
import com.training.util.QrCodeUtil;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QrCodeController {

    private final UserMapper userMapper;
    private final AuthService authService;

    // 从配置文件读取前端域名（推荐）
    @Value("${app.frontend.domain:http://172.25.8.77:3000}")
    private String frontendDomain;

    /**
     * 生成签到二维码（仅管理员）
     * 示例请求: GET /api/qrcode?trainingId=5
     * 返回: PNG 图片
     */
    @GetMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateCheckinQrCode(
            @RequestParam Long trainingId,
            HttpServletRequest request) throws Exception {
        // 检查管理员权限
        checkAdminPermission(request);

        // 构造签到页面 URL
        String checkinUrl = frontendDomain + "/checkin?training_id=" + trainingId;

        // 生成二维码（300x300 像素）
        byte[] qrCodeImage = QrCodeUtil.generateQrCode(checkinUrl, 300, 300);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrCodeImage.length);

        return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
    }

    /**
     * 生成签退二维码（仅管理员）
     * 示例请求: GET /api/qrcode/checkout?trainingId=5
     * 返回: PNG 图片
     */
    @GetMapping(value = "/qrcode/checkout", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateCheckoutQrCode(
            @RequestParam Long trainingId,
            HttpServletRequest request) throws Exception {
        // 检查管理员权限
        checkAdminPermission(request);

        // 构造签退页面 URL
        String checkoutUrl = frontendDomain + "/checkout?training_id=" + trainingId;

        // 生成二维码（300x300 像素）
        byte[] qrCodeImage = QrCodeUtil.generateQrCode(checkoutUrl, 300, 300);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrCodeImage.length);

        return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
    }

    /**
     * 检查当前用户是否为管理员
     */
    private void checkAdminPermission(HttpServletRequest request) {
        // 优先从request attribute获取（拦截器设置的）
        Long userId = (Long) request.getAttribute("currentUserId");
        
        // 如果attribute中没有，则从请求头中解析token
        if (userId == null) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new BizException(ErrorCode.NOT_FOUND, "未登录");
            }
            String token = authHeader.substring(7);
            userId = authService.parseToken(token);
            if (userId == null) {
                throw new BizException(ErrorCode.NOT_FOUND, "未登录或token无效");
            }
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        if (!"admin".equals(user.getRole())) {
            throw new BizException(ErrorCode.BUSINESS_CONFLICT, "仅管理员可以生成二维码");
        }
    }
}
