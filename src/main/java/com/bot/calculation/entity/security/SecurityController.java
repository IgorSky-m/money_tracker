package com.bot.calculation.entity.security;

import com.bot.calculation.entity.security.api.ISecurityService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class SecurityController {

    private final ISecurityService securityService;

    public SecurityController(ISecurityService securityService){
        this.securityService = securityService;
    }


//
//    //TODO переписать в конструктор
//    @Value("${app.name}")
//    private String appName;
//
//    //TODO убрать в проперти? (передавать извне) это тест
//    private final String secretKey = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK";
//
//    @PostMapping
//    public void createBarcode(@RequestParam String email, HttpServletRequest request, HttpServletResponse response) throws IOException, WriterException {
//        Cookie[] cookies = request.getCookies();
//        Cookie cookie = new Cookie("auth", "true");
//        cookie.setMaxAge(10000);
//        Cookie cookie2 = new Cookie("short", "yes");
//        cookie2.setMaxAge(10);
//        response.addCookie(cookie);
//        response.addCookie(cookie2);
//        String barCodeUrl = Utils.getGoogleAuthenticatorBarCode(secretKey, email, appName);
//        System.out.println(barCodeUrl);
//        response.setContentType(Disposition.BARCODE_CONTENT_TYPE);
//        response.setHeader(Disposition.HEADER_PARAM_NAME, String.format("%s; filename=\"%s\"", Disposition.INLINE, "nameFile"));
//        Utils.createQRCode(barCodeUrl, Disposition.BARCODE_SIZE, Disposition.BARCODE_SIZE, response);
//    }

//    //TODO переделать адекватно
//    @GetMapping
//    public ResponseEntity<String> checkCode(@RequestParam String code) {
//        String res;
//        HttpStatus status;
//        URI uri = null;
//        if (code.equals(Utils.getTOTPCode(secretKey))) {
//            res = "Logged in successfully";
//            status = HttpStatus.OK;
//        } else {
//            status = HttpStatus.FOUND;
//            uri = URI.create("https://fullstackdeveloper.guru");
//            res = "Invalid 2FA Code";
//        }
//
//        ResponseEntity.BodyBuilder builder = ResponseEntity.status(status);
//        if (uri != null) {
//            builder.location(uri);
//        }
//
//        return builder.body(res);
//    }

    @PostMapping
    public void pin(@RequestBody PinCode pinCode){
        this.securityService.authorize(pinCode.pinCode);
    }

    @Getter
    @Setter
    private static class PinCode {
        private String pinCode;
    }

    //TODO в проперти и сделать проперти класс
    private static class Disposition{
        public static final String HEADER_PARAM_NAME = "Content-Disposition";
        public static final String ATTACHMENT = "attachment";
        public static final String INLINE = "inline";
        public static final String BARCODE_CONTENT_TYPE = "image/png";
        public static final int BARCODE_SIZE = 300;

        public static boolean isValid(String disposition) {
            return ATTACHMENT.equals(disposition) || INLINE.equals(disposition);
        }

    }

}
