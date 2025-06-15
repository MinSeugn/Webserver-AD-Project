package com.mysite.sbb.answer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        return this.answerRepository.save(answer);
    }

    public Answer getAnswer(Integer id) {
        return this.answerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("answer not found"));
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    public void vote(Answer answer, SiteUser siteUser) {
        if (!answer.getVoter().contains(siteUser)) {
            answer.getVoter().add(siteUser);
            answer.setVoteCount(answer.getVoter().size());
            this.answerRepository.save(answer);
        }
    }

    public Page<Answer> getAnswersByQuestion(Question question, int page, String sort) {
        Pageable pageable;
        if ("recommend".equals(sort)) {
            pageable = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("voteCount"), Sort.Order.desc("createDate")));
        } else {
            pageable = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("createDate")));
        }
        return answerRepository.findByQuestion(question, pageable);
    }

}
