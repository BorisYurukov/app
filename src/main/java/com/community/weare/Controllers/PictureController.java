package com.community.weare.Controllers;

import com.community.weare.Models.User;
import com.community.weare.Services.users.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
@Controller
@RequestMapping("/")
public class PictureController {
    private final UserService userService;

    @Autowired
    public PictureController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("users/{id}/userImage")
    public void renderPostImageFormDB(@PathVariable int id, HttpServletResponse response) throws IOException {
        User user = userService.getUserById(id);
        if (user.getPersonalProfile().getPicture() != null) {
            response.setContentType("image/jpeg");
            InputStream is = new ByteArrayInputStream(Base64.getDecoder().
                    decode(user.getPersonalProfile().getPicture()));
            IOUtils.copy(is, response.getOutputStream());
        }
    }
}
