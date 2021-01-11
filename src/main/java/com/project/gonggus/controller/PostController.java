package com.project.gonggus.controller;

import com.project.gonggus.domain.post.PostDto;
import com.project.gonggus.domain.post.PostService;
import com.project.gonggus.domain.user.AuthService;
import com.project.gonggus.domain.user.User;
import com.project.gonggus.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "https://www.gonggus.cf", allowCredentials = "true")
public class PostController {

    @Autowired
    private final PostService postService;

    @Autowired
    private final UserService userService;

    @Autowired
    AuthService authService;

    @GetMapping("/{category}")
    public List<PostDto> categoryPost(@PathVariable("category") String category){
        return postService.getCategoryPosts(category);
    }

    @GetMapping("/searchpost")
    public List<PostDto> searchPost(@RequestParam("search") String searchTitle) {
        return postService.getSearchPosts(searchTitle);
    }

    @RequestMapping("/makepost")
    public void makePost(HttpServletRequest res){
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
    }

    @PostMapping("/makepostsubmit")
    public void makePostSubmit(@RequestBody Map<String, String> param,
                               HttpServletRequest res){
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        postService.savePost(user.getUserId(), param.get("title"),
                param.get("content"),
                param.get("category"),
                param.get("goodsLink"),
                param.get("limitNumberOfPeople"),
                param.get("deadline"));
    }

    @GetMapping("/post/{postId}/update")
    public PostDto updatePost(@PathVariable("postId") Long postId){
        return postService.getPostDto(postId);
    }

    @PutMapping("/updatepostsubmit/{postId}")
    public void updatePostSubmit(@PathVariable("postId") Long postId,
                                 @RequestBody Map<String, String> param,
                                 HttpServletRequest res){
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        postService.updatePost(postId, param.get("title"), param.get("content"), param.get("category"),param.get("goodsLink"),param.get("limitNumberOfPeople"),param.get("deadline"));
    }


    @GetMapping("/post/{postId}")
    public PostDto detailPost(@PathVariable("postId") Long postId){
        return postService.getPostDto(postId);
    }


    @PostMapping("/post/{postId}/registerbookmark")
    public void registerBookmark(@PathVariable("postId") Long postId,
                                 HttpServletRequest res){
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        postService.registerBookmark(user.getUserId(), postId);
    }

    @DeleteMapping("/post/{postId}/deletebookmark")
    public void deleteBookmark(@PathVariable("postId") Long postId,
                               HttpServletRequest res){
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        postService.deleteBookmark(user.getUserId(), postId);
    }

    @PostMapping("/post/{postId}/participatepost")
    public void participatePost(@PathVariable("postId") Long postId,
                                HttpServletRequest res){
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        postService.participatePost(user.getUserId(), postId);
    }

    @DeleteMapping("/post/{postId}/withdrawpost")
    public void withdrawPost(@PathVariable("postId") Long postId,
                             HttpServletRequest res){
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        postService.withdrawPost(user.getUserId(), postId);
    }

    @DeleteMapping("/post/{postId}/deletepost")
    public void deletePost(@PathVariable("postId") Long postId,
                           HttpServletRequest res){
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        postService.deletePost(user.getUserId(), postId);
    }
}

