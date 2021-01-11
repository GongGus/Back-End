package com.project.gonggus.controller;

import com.project.gonggus.domain.post.PostDto;
import com.project.gonggus.domain.post.PostService;
import com.project.gonggus.domain.user.*;
import com.project.gonggus.domain.userpost.UserPost;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "https://api.gonggus.cf", allowCredentials = "true")
public class UserContoller {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    PostService postService;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> signIn (
            @RequestBody Map<String, Object> body, HttpServletResponse res) {
        Map<String, Object> resultMap = new HashMap<>();

        String userId = body.get("userId").toString();
        String userPassword = body.get("userPassword").toString();

        Optional<User> user = userService.getUserByUserId(userId);
        if(user.isPresent()) {
            User u = user.get();
            if(!authService.encryptString(userPassword).equals(u.getUserPassword())) {
                return ResponseEntity.badRequest().build();
            }
            resultMap.put("userData", UserDto.convert(u));

            String token = authService.create(u);

            res.addCookie(authService.setAuthCookie(token));
            resultMap.put("token", token);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resultMap);
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> signOut(HttpServletResponse res){
        // JWT 토큰과 같은 이름의 토큰을 만료된 시간으로 설정하여 저장.
        res.addCookie(authService.removeCookie());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        String name = body.get("name").toString();
        String userId = body.get("userId").toString();
        String userPassword = body.get("userPassword").toString();
        String nickname = body.get("nickname").toString();
        String schoolName = body.get("schoolName").toString();

        if (userService.getUserByUserId(userId).isPresent()){
            return ResponseEntity.badRequest().build();
        } else {
            userService.join(new User(name, userId, userPassword, nickname, schoolName));
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/check")
    @ResponseBody
    public ResponseEntity<?> check(HttpServletRequest res){
        Map<String, Object> resultMap = new HashMap<>();

        for (Cookie c : res.getCookies()) {
            if (c.getName().equals("access_token")) {
                String token = c.getValue();
                Map<String, Object> authResponse = authService.get(token);

                String userId = authResponse.get("userId").toString();
                Optional<User> user = userService.getUserByUserId(userId);
                if(user.isPresent()) {
                    User u = user.get();
                    resultMap.put("userData", UserDto.convert(u));
                    resultMap.put("token", token);
                } else {
                    return ResponseEntity.badRequest().build();
                }

                return ResponseEntity.ok(resultMap);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/mypage/update")
    @ResponseBody
    public ResponseEntity<?> update(HttpServletRequest res, @RequestBody Map<String, Object> body){
        Map<String, Object> resultMap = new HashMap<>();

        String name = body.get("name").toString();
        String nickname = body.get("nickname").toString();

        for (Cookie c : res.getCookies()) {
            if (c.getName().equals("access_token")) {
                String token = c.getValue();
                Map<String, Object> authResponse = authService.get(token);

                String userId = authResponse.get("userId").toString();
                Optional<User> user = userService.getUserByUserId(userId);
                if(user.isPresent()) {
                    User u = user.get();
                    u.setName(name);
                    u.setNickname(nickname);
                    userService.save(u);
                    resultMap.put("userData", UserDto.convert(u));
                    resultMap.put("token", token);
                } else {
                    return ResponseEntity.badRequest().build();
                }

                return ResponseEntity.ok(resultMap);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/mypage/bookmark")
    @ResponseBody
    public ResponseEntity<?> bookmark(HttpServletRequest res) {
        List<PostDto> result = new ArrayList<>();
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        if (!user.getBookmarkPosts().isEmpty()) {
            for (Long id : user.getBookmarkPosts()) {
                result.add(postService.getPostDto(id));
            }
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/mypage/participate")
    @ResponseBody
    public ResponseEntity<?> participate(HttpServletRequest res) {
        List<PostDto> result = new ArrayList<>();
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        if (!user.getParticipatePosts().isEmpty()) {
            for (UserPost post : user.getParticipatePosts()) {
                result.add(PostDto.convert(post.getPost()));
            }
        }
        return ResponseEntity.ok(result);
    }
}
