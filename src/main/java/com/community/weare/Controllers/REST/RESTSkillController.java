package com.community.weare.Controllers.REST;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Skill;
import com.community.weare.Services.models.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/skill")
public class RESTSkillController {
    private SkillService skillService;

    @Autowired
    public RESTSkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public List<Skill> findAll(Sort sort) {
        return skillService.findAll(Sort.by(Sort.Direction.ASC, "skill"));
    }

    @GetMapping("/getOne")
    public Skill getOne(@RequestParam int skillId) {
        try {
            return skillService.getOne(skillId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/create")
    public Skill save(@RequestBody Skill skill) {
        return skillService.save(skill);
    }

    @PutMapping("/edit")
    public void editSkill(@RequestParam int skillId, String skill) {
        try {
            skillService.editSkill(skillId, skill);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/delete")
    public void deleteSkill(int skillId) {
        try {
            skillService.deleteSkill(skillId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
