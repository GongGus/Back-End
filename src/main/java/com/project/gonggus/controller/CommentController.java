package com.project.gonggus.controller;

import com.project.gonggus.domain.comment.CommentDto;
import com.project.gonggus.domain.comment.CommentService;
import com.project.gonggus.domain.user.AuthService;
import com.project.gonggus.domain.user.User;
import com.project.gonggus.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "https://api.gonggus.cf", allowCredentials = "true")
public class CommentController {

    @Autowired
    private final CommentService commentService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final AuthService authService;

    @PostMapping("/post/{postId}/writecommentsubmit")
    public CommentDto writeCommentSubmit(@PathVariable("postId") Long postId,
                                         @RequestBody Map<String, String> param,
                                         HttpServletRequest res){
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        Long commentId = commentService.saveComment(user.getUserId(), postId, param.get("content"), Boolean.valueOf(param.get("isEdit")), param.get("createdDate"));
        return commentService.getCommentDto(commentId);
    }

    @GetMapping("/post/{postId}/updatecomment/{commentId}")
    public CommentDto updateComment(@PathVariable("postId") Long postId,
                                    @PathVariable("commentId") Long commentId,
                                    HttpServletRequest res){
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        return commentService.getCommentDto(commentId);
    }

    @PutMapping("/post/{postId}/updatecommentsubmit/{commentId}")
    public void updateCommentSubmit(@PathVariable("postId") Long postId,
                                    @PathVariable("commentId") Long commentId,
                                    @RequestBody Map<String, String> param,
                                    HttpServletRequest res){
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        commentService.updateComment(user.getUserId(), postId, commentId, param.get("content"));
    }

    @DeleteMapping("/post/{postId}/deletecomment/{commentId}")
    public void deleteComment(@PathVariable("commentId") Long commentId,
                              HttpServletRequest res){
        User user = userService.getUserByCookie(authService.findAuthCookie(res.getCookies()));
        commentService.deleteComment(user.getUserId(), commentId); //같아서 삭제되는 경우
    }

}