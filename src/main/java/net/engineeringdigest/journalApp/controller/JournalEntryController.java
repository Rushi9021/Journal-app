package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser()
    {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName=authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> k= user.getJournalEntries();
        if(k!=null && !k.isEmpty())
        {
            return new ResponseEntity<>(k,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry)
    {
        try {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName=authentication.getName();
            journalEntryService.saveEntry(myEntry ,userName);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);

        }catch (Exception e)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("id/{myID}")
    public ResponseEntity<JournalEntry> findEntryByID(@PathVariable ObjectId myID)
    {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName=authentication.getName();
        User user= userService.findByUserName(userName);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x->x.getId().equals(myID)).collect(Collectors.toList());
        if(!collect.isEmpty())
        {
            Optional<JournalEntry> journalEntry= journalEntryService.findById(myID);
            if(journalEntry.isPresent())
            {
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{myID}")
    public ResponseEntity<?> deleteEntryByID(@PathVariable ObjectId myID)
    {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName=authentication.getName();
        boolean removed=journalEntryService.deleteByID(myID, userName);
        if(removed)
        {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping("id/{myID}")
    public ResponseEntity<?> updateEntry(
            @PathVariable ObjectId myID ,
            @RequestBody JournalEntry updateEntry
    )
    {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName=authentication.getName();
        User user= userService.findByUserName(userName);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x->x.getId().equals(myID)).collect(Collectors.toList());
        if(!collect.isEmpty())
        {
            Optional<JournalEntry> journalEntry= journalEntryService.findById(myID);
            if(journalEntry.isPresent())
            {
                JournalEntry old = journalEntry.get();
                old.setTitle(updateEntry.getTitle()!=null && !updateEntry.getTitle().isEmpty() ? updateEntry.getTitle() : old.getTitle());
                old.setContent(updateEntry.getContent()!=null && !updateEntry.getTitle().isEmpty() ? updateEntry.getContent() : old.getContent());
                journalEntryService.saveEntry(old);
                return new ResponseEntity<>(old,HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}