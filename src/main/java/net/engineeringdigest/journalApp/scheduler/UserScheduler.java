package net.engineeringdigest.journalApp.scheduler;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.UserRepositoryImpl;
import net.engineeringdigest.journalApp.service.EmailService;
import net.engineeringdigest.journalApp.service.SentimentAnalysisService;
import net.engineeringdigest.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserScheduler {


    @Autowired
    private UserService userService;

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Scheduled(cron = "0 0 9 * * SUN")
    public void fetchUserAndSendSaMail(){
        final List<User> userForSA = userRepositoryImpl.getUserForSA();
        for(User user : userForSA){
            final List<JournalEntry> journalEntries = user.getJournalEntries();
            final List<String> collect = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x->x.getContent()).collect(Collectors.toList());
            String entry=String.join(" ",collect);
            final String sentiment = sentimentAnalysisService.getSentiment();
            emailService.sendEmail(user.getEmail(), "Sentiment for last 7 Days: " ,sentiment);
        }
    }
}