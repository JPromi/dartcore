package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.AccountGroup;
import com.jpromi.darts.backend.entities.AccountGroupMember;
import com.jpromi.darts.backend.entities.DartThrow;
import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.ThrowType;
import com.jpromi.darts.backend.models.LoginRequest;
import com.jpromi.darts.backend.repositories.AccountGroupMemberRepository;
import com.jpromi.darts.backend.repositories.AccountGroupRepository;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.services.CheckAchievementService;
import com.jpromi.darts.backend.services.TemplateService;
import com.jpromi.darts.backend.services.TotpService;
import com.password4j.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController("")
@RequestMapping("/api")
public class TestController {

    @Autowired
    private CheckAchievementService checkAchievementService;

    @Autowired
    private TotpService totpService;

    @Autowired
    private AccountGroupRepository accountGroupRepository;

    @Autowired
    private AccountGroupMemberRepository accountGroupMemberRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TemplateService templateService;

    @GetMapping("/ach")
    public String test() {
        List<DartThrow> dartThrows = List.of(
                new DartThrow(null, null, null, DartThrowMultiplierEnum.TRIPLE, ThrowType.THROW, 20, 1, false, false, LocalDateTime.now()),
                new DartThrow(null, null, null, DartThrowMultiplierEnum.TRIPLE, ThrowType.THROW, 20, 1, false, false, LocalDateTime.now()),
                new DartThrow(null, null, null, DartThrowMultiplierEnum.TRIPLE, ThrowType.THROW, 20, 1, false, false, LocalDateTime.now())
        );
        String condition = "ANY:T20;ANY:T20;ANY:T20";

        return checkAchievementService.checkAchievement(condition, dartThrows).toString();
    }

    @PostMapping("/argon2")
    public String testArgon2(@RequestBody String request) {
        return Password.hash(request).withArgon2().getResult();
    }

    @GetMapping("/rec-totp")
    public String[] recTotp() {
        return totpService.generateRecoveryCodes();
    }

    @GetMapping("/new-group")
    public AccountGroup createGroup() {
//        accountGroupRepository.deleteAll();
        Account account = accountRepository.findByUsername("test");
        AccountGroupMember accountGroupMember = AccountGroupMember.builder()
                .account(account)
                .accountGroup(null)
                .build();

        AccountGroup accountGroup = AccountGroup.builder()
                .name("Test Group")
                .description("Test Group Description")
                .isPublic(true)
                .avatar(null)
                .members(List.of(accountGroupMember))
                .build();

        accountGroupMember.setAccountGroup(accountGroup);

        accountGroupRepository.save(accountGroup);
//        accountGroupMemberRepository.save(accountGroupMember);
        return accountGroup;
    }

    @GetMapping("/user")
    public String account() {

        return accountRepository.findByUsername("test").getGroupMemberships().getFirst().getAccountGroup().getName();
    }

    @GetMapping("/html")
    public String html() {
        String templatePath = "src/main/resources/templates/mail/registration.html";
        String rootPath = "src/main/resources/templates/mail/container.html";

        String content = templateService.generateTemplateFromFile(templatePath, new HashMap<>(Map.of(
                "title", "Test Title",
                "content", "Test Content"
        )));

        return templateService.generateTemplateFromFile(rootPath, new HashMap<>(Map.of(
                "title", "Test Title",
                "content", content,
                "currentYear", "2025",
                "urlWeb", "https://jpromi.com",
                "confirmationLink", "https://dev.jpromi.com/barrier/register/1234567890"
        )));
    }

    @GetMapping("/groupcount/{uuid}")
    public Long groupCount(@PathVariable UUID uuid) {
        return accountGroupRepository.countActiveMembersByGroupUuid(uuid);
    }
}
