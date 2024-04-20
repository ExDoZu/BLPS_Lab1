package com.blps.lab1.model.services;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;

import com.blps.lab1.exceptions.AccessDeniedException;
import com.blps.lab1.exceptions.NotFoundException;
import com.blps.lab1.model.beans.Post;
import com.blps.lab1.model.beans.User;
import com.blps.lab1.model.repository.PostRepository;
import com.blps.lab1.model.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public class ModerationResult {
        public List<Post> posts;
        public int totalPages;

        public ModerationResult(List<Post> posts, int totalPages) {
            this.posts = posts;
            this.totalPages = totalPages;
        }

        public List<Post> getPosts() {
            return posts;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }

    public ModerationResult getModerationPosts(int page, int size, String moderatorPhone) {
        User me = userRepository.findByPhoneNumber(moderatorPhone);
        if (me == null) {
            throw new AccessDeniedException("User not found");
        }

        if (!me.isModerator()) {
            throw new AccessDeniedException("Not a moderator");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByArchivedAndApproved(false, null, pageable);
        List<Post> posts = postPage.getContent();

        return new ModerationResult(posts, postPage.getTotalPages());
    }

    public void approve(long postId, String moderatorPhone, boolean approved)
            throws AccessDeniedException, NotFoundException {
        User me = userRepository.findByPhoneNumber(moderatorPhone);
        if (me == null) {
            throw new AccessDeniedException("User not found");
        }

        if (!me.isModerator()) {
            throw new AccessDeniedException("Not a moderator");
        }

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException("Post not found");
        }

        post.setApproved(approved);
        if (approved) {
            post.setArchived(false);
        }

        postRepository.save(post);
    }
}
