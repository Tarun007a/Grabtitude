package com.project.grabtitude.services.impl;

import com.project.grabtitude.dto.*;
import com.project.grabtitude.entity.*;
import com.project.grabtitude.helper.AuthUtil;
import com.project.grabtitude.helper.CustomPageResponse;
import com.project.grabtitude.helper.ResourceNotFoundException;
import com.project.grabtitude.mapper.Mapper;
import com.project.grabtitude.mapper.impl.SubmissionResponseMapper;
import com.project.grabtitude.repository.*;
import com.project.grabtitude.services.ProblemOptionService;
import com.project.grabtitude.services.ProblemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class ProblemServiceImpl implements ProblemService {
    private final ProblemRepo problemRepo;
    private final Mapper<ProblemOption, ProblemOptionRequestDto> problemOptionRequestMapper;
    private final Mapper<ProblemOption, ProblemOptionResponseDto> problemOptionResponseMapper;
    private final Mapper<Problem, ProblemResponseDto> problemResponseDtoMapper;
    private final Mapper<Problem, ProblemRequestDto> problemRequestDtoMapper;
    private final ProblemOptionRepo problemOptionRepo;
    private final TopicRepo topicRepo;
    private final ProblemOptionService problemOptionService;
    private final AuthUtil authUtil;
    private final UserRepo userRepo;
    private final SubmissionRepo submissionRepo;
    private final SubmissionResponseMapper submissionResponseMapper;
    private final CategoryRepo categoryRepo;
    public ProblemServiceImpl(ProblemRepo problemRepo, ProblemOptionRepo problemOptionRepo,
                              Mapper<ProblemOption, ProblemOptionRequestDto> problemOptionRequestMapper,
                              Mapper<ProblemOption, ProblemOptionResponseDto> problemOptionResponseMapper,
                              Mapper<Problem, ProblemResponseDto> problemResponseDtoMapper,
                              Mapper<Problem, ProblemRequestDto> problemRequestDtoMapper,
                              TopicRepo topicRepo, ProblemOptionService problemOptionService,
                              AuthUtil authUtil, UserRepo userRepo, SubmissionRepo submissionRepo,
                              SubmissionResponseMapper submissionResponseMapper,
                              CategoryRepo categoryRepo
    ){
        this.problemRepo = problemRepo;
        this.problemOptionRepo = problemOptionRepo;
        this.problemOptionRequestMapper = problemOptionRequestMapper;
        this.problemOptionResponseMapper = problemOptionResponseMapper;
        this.problemRequestDtoMapper = problemRequestDtoMapper;
        this.problemResponseDtoMapper = problemResponseDtoMapper;
        this.topicRepo = topicRepo;
        this.problemOptionService = problemOptionService;
        this.authUtil = authUtil;
        this.userRepo = userRepo;
        this.submissionRepo = submissionRepo;
        this.submissionResponseMapper = submissionResponseMapper;
        this.categoryRepo = categoryRepo;
    }

    @Override
    @Transactional
    public ProblemResponseDto createProblem(ProblemRequestDto problemRequestDto) {
        System.out.println(problemRequestDto);
        List<ProblemOptionRequestDto> problemOptionDtos = problemRequestDto.getOptions();
        long topicId = problemRequestDto.getTopicId();
        long categoryId = problemRequestDto.getCategoryId();

        int totalTrue = 0;
        for(ProblemOptionRequestDto option : problemOptionDtos){
            if(option.getCorrect()) totalTrue++;
        }
        if(totalTrue != 1) throw new RuntimeException("One true option should be present");

        Topic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id : " + topicId));
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id : " + categoryId));

        Problem problem = problemRequestDtoMapper.mapFrom(problemRequestDto);
        problem.setTopic(topic);
        problem.setCategory(category);
        Problem savedProblem = problemRepo.save(problem);
        ProblemResponseDto problemResponseDto = problemResponseDtoMapper.mapTo(savedProblem);

        List<ProblemOptionResponseDto> savedProblemOptionDtos = new ArrayList<>();
        for(ProblemOptionRequestDto optionDto : problemOptionDtos){
            ProblemOption option = problemOptionRequestMapper.mapFrom(optionDto);
            option.setProblem(savedProblem);
            ProblemOption savedOption = problemOptionRepo.save(option);
            savedProblemOptionDtos.add(problemOptionResponseMapper.mapTo(savedOption));
        }

        problemResponseDto.setOptions(savedProblemOptionDtos);
        problemResponseDto.setTopicName(topic.getName());
        return problemResponseDto;
    }

    @Override
    public ProblemResponseDto getById(Long id) {
        Problem problem = problemRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with problem id : " + id));
        List<ProblemOptionResponseDto> problemOptionDtos = problemOptionService.getOptionForProblem(problem);

        ProblemResponseDto problemResponseDto = problemResponseDtoMapper.mapTo(problem);
        problemResponseDto.setOptions(problemOptionDtos);
        problemResponseDto.setCategoryId(problem.getCategory().getId());
        problemResponseDto.setTopicName(problem.getTopic().getName());
        problemResponseDto.setAcceptance((int)((double)problem.getCorrectSubmissions()/(double) problem.getTotalSubmissions())*100);        return problemResponseDto;
    }

    @Override
    public CustomPageResponse<ProblemResponseDto> getProblems(int page, int size) {
        Sort sort = Sort.by("problemId").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Problem> problems = problemRepo.findAll(pageable);

        //now problem is we got the page of problem from repo but we need to return page of ProblemResponseDto
        Page<ProblemResponseDto> problemResponseDtoPage = problems.map(problem -> {
            ProblemResponseDto problemResponseDto = problemResponseDtoMapper.mapTo(problem);
            List<ProblemOptionResponseDto> problemOptionDtos = problemOptionService.getOptionForProblem(problem);
            problemResponseDto.setOptions(problemOptionDtos);
            problemResponseDto.setTopicName(problem.getTopic().getName());
            problemResponseDto.setCategoryId(problem.getCategory().getId());
            problemResponseDto.setAcceptance((int)((double)problem.getCorrectSubmissions()/(double) problem.getTotalSubmissions())*100);            return problemResponseDto;
        });

        CustomPageResponse<ProblemResponseDto> responsePage = new CustomPageResponse<>();

        responsePage.setContent(problemResponseDtoPage.getContent());
        responsePage.setPageNumber(problemResponseDtoPage.getNumber());
        responsePage.setPageSize(problemResponseDtoPage.getSize());
        responsePage.setLast(problemResponseDtoPage.isLast());
        responsePage.setFirst(problemResponseDtoPage.isFirst());
        responsePage.setTotalPages(problemResponseDtoPage.getTotalPages());
        responsePage.setTotalNumberOfElements(problemResponseDtoPage.getTotalElements());
        responsePage.setNumberOfElements(problemResponseDtoPage.getNumberOfElements());

        String email = authUtil.getEmailOfLoggedUser();
        Optional<User> userOptional = userRepo.findByEmail(email);
        userOptional.ifPresent(user -> markSolvedAttemptedUnsolved(responsePage, user));

        return responsePage;
    }

    @Override
    public SubmissionResponseDto submit(SubmissionRequestDto submissionRequestDto) {
        String email = authUtil.getEmailOfLoggedUser();

//      Optional<User> userOptional = userRepo.findByEmail(email);
//      if(userOptional.isEmpty()) throw new ResourceNotFoundException("Please login and logout again");
//      to reduce this, the above one i.e. first getting optional then if it is empty throw error we can do it
//      in one line using this, here if we get null we throw or if we get something we directly extract it

        User user = userRepo.findByEmail(email)
               .orElseThrow(() -> new ResourceNotFoundException("Please login and logout again"));

        ProblemOption problemOption = problemOptionRepo.findById(submissionRequestDto.getOptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Please enter or select a valid option id"));

        Problem problem = problemRepo.findById(submissionRequestDto.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("No such problem exist which you are trying to submit"));

        if(!problemOption.getProblem().getProblemId().equals(submissionRequestDto.getProblemId())){
            throw new ResourceNotFoundException("Please enter correct problemId and optionId");
        }

        boolean isFirstForToday = updateStreak(user);

        problem.setTotalSubmissions(problem.getTotalSubmissions()+1);
        if(problemOption.getCorrect()) problem.setCorrectSubmissions(problem.getCorrectSubmissions()+1);

        Submission submission = new Submission();
        submission.setProblem(problem);
        submission.setUser(user);
        submission.setSelectedOption(problemOption);
        submission.setCorrect(problemOption.getCorrect());

        submissionRepo.save(submission);

        SubmissionResponseDto submissionResponseDto = submissionResponseMapper.mapTo(submission);
        submissionResponseDto.setSubmissionId(submission.getId());
        submissionResponseDto.setProblemId(submission.getProblem().getProblemId());
        return submissionResponseDto;
    }

    private boolean updateStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        if(user.getLastSubmittedAt() != null && user.getLastSubmittedAt().isEqual(yesterday)){
            user.setStreak(user.getStreak()+1);
            user.setMaxStreak(Math.max(user.getStreak(), user.getMaxStreak()));
            user.setLastSubmittedAt(today);
            return true;
        }
        else {
            user.setLastSubmittedAt(today);
            user.setStreak(1);
            user.setMaxStreak(Math.max(user.getMaxStreak(), user.getStreak()));
        }
        userRepo.save(user);
        return false;
    }

    @Override
    public CustomPageResponse<ProblemResponseDto> search(String keyword, int page, int size) {
        int n = keyword.length();
        if(n > 100) throw new ResourceNotFoundException("Keyword size cannot be greater than 100");

        Pageable pageable = PageRequest.of(page, size);
        Page<Problem> problemPage = problemRepo.findByTitleContaining(keyword, pageable);
        Page<ProblemResponseDto> problemResponseDtoPage = problemPage.map(problem -> {
            ProblemResponseDto problemResponseDto = problemResponseDtoMapper.mapTo(problem);
            List<ProblemOptionResponseDto> optionForProblem = problemOptionService.getOptionForProblem(problem);
            problemResponseDto.setOptions(optionForProblem);
            problemResponseDto.setTopicName(problem.getTopic().getName());
            problemResponseDto.setCategoryId(problem.getCategory().getId());
            problemResponseDto.setAcceptance((int)((double)problem.getCorrectSubmissions()/(double) problem.getTotalSubmissions())*100);            return problemResponseDto;
        });

        CustomPageResponse<ProblemResponseDto> responsePage = new CustomPageResponse<>();

        responsePage.setContent(problemResponseDtoPage.getContent());
        responsePage.setPageNumber(problemResponseDtoPage.getNumber());
        responsePage.setPageSize(problemResponseDtoPage.getSize());
        responsePage.setLast(problemResponseDtoPage.isLast());
        responsePage.setFirst(problemResponseDtoPage.isFirst());
        responsePage.setTotalPages(problemResponseDtoPage.getTotalPages());
        responsePage.setTotalNumberOfElements(problemResponseDtoPage.getTotalElements());
        responsePage.setNumberOfElements(problemResponseDtoPage.getNumberOfElements());

        //if keyword is empty so it will get all so we not return at top and return here as we access 0th index
        if(keyword.isEmpty()) return responsePage;

        int ascii = (int)(keyword.charAt(0)-'0');
        if(ascii >= 0 && ascii <= 9){
            long searchId = 0L;
            int digits = 5;
            for(char ch : keyword.toCharArray()){
                int curr = (int)(ch-'0');
                if(curr >= 0 && curr <= 9) {
                    searchId = searchId * 10L + curr;
                    digits--;
                }
                if(digits == 0)break;
            }
            List<ProblemResponseDto> content = new ArrayList<>(problemResponseDtoPage.getContent());

            Optional<Problem> problemOptional = problemRepo.findById(searchId);
            if(problemOptional.isPresent()) {
                Problem problem = problemOptional.get();
                ProblemResponseDto problemResponseDto = problemResponseDtoMapper.mapTo(problem);
                List<ProblemOptionResponseDto> problemOptionDtos = problemOptionService.getOptionForProblem(problem);

                problemResponseDto.setOptions(problemOptionDtos);
                problemResponseDto.setTopicName(problem.getTopic().getName());
                problemResponseDto.setCategoryId(problem.getCategory().getId());
                problemResponseDto.setAcceptance((int)((double)problem.getCorrectSubmissions()/(double) problem.getTotalSubmissions())*100);
                content.add(0, problemResponseDto);
                responsePage.setNumberOfElements(responsePage.getNumberOfElements()+1);
                responsePage.setTotalNumberOfElements(responsePage.getTotalNumberOfElements()+1);
                responsePage.setContent(content);
            }
        }

        String email = authUtil.getEmailOfLoggedUser();
        Optional<User> userOptional = userRepo.findByEmail(email);
        userOptional.ifPresent(user -> markSolvedAttemptedUnsolved(responsePage, user));

        return responsePage;
    }

    @Override
    @Transactional
    public ProblemResponseDto update(ProblemUpdateDto problemUpdateDto) {
        Optional<Problem> problemOptional = problemRepo.findById(problemUpdateDto.getProblemId());
        if(problemOptional.isEmpty()) throw new ResourceNotFoundException("Problem with problem id " + problemUpdateDto.getProblemId() +
                " does not exist, please enter a valid problem id or create one");

        Optional<Topic> topicOptional = topicRepo.findById(problemUpdateDto.getTopicId());
        if(topicOptional.isEmpty()) throw new ResourceNotFoundException("Topic with topic id " + problemUpdateDto.getTopicId() +
                " does not exist, please enter a valid topic id or create one");

        Category category = categoryRepo.findById(problemUpdateDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id : " + problemUpdateDto.getCategoryId()));

        Problem problem = problemOptional.get();
        Topic topic = topicOptional.get();

        problem.setTitle(problemUpdateDto.getTitle());
        problem.setCategory(category);
        String difficulty = problemUpdateDto.getDifficulty();
        problem.setDescription(problemUpdateDto.getDescription());
        problem.setTopic(topic);
        problem.setExplanation(problemUpdateDto.getExplanation());
        List<ProblemOptionResponseDto> updatedOptions = new ArrayList<>();

        if(difficulty.equals("EASY")) problem.setDifficulty(Problem.Difficulty.EASY);
        else if(difficulty.equals("MEDIUM")) problem.setDifficulty(Problem.Difficulty.MEDIUM);
        else if(difficulty.equals("HARD")) problem.setDifficulty(Problem.Difficulty.HARD);
        else if(difficulty.equals("EXPERT")) problem.setDifficulty(Problem.Difficulty.EXPERT);
        else throw new ResourceNotFoundException("Please enter a valid difficulty");

        ProblemResponseDto problemResponseDto = problemResponseDtoMapper.mapTo(problem);
        int totalCorrect = 0;
        //update options
        for(ProblemOptionUpdateDto problemOptionUpdateDto : problemUpdateDto.getOptions()){
            if(problemOptionUpdateDto.getCorrect()) totalCorrect++;
            Optional<ProblemOption> optionOptional = problemOptionRepo.findById(problemOptionUpdateDto.getId());
            if(optionOptional.isEmpty()) throw new ResourceNotFoundException("Problem option with id : " + problemOptionUpdateDto.getId() + " not found");
            ProblemOption problemOption = optionOptional.get();

            if(problemOption.getProblem().getProblemId() != problem.getProblemId()){
                throw new RuntimeException("The option id entered to update is not the option for given problem with problem id : " + problem.getProblemId());
            }
            problemOption.setContent(problemOptionUpdateDto.getContent());
            problemOption.setCorrect(problemOptionUpdateDto.getCorrect());
            ProblemOption savedOption = problemOptionRepo.save(problemOption);

            updatedOptions.add(problemOptionResponseMapper.mapTo(savedOption));
        }
        if(totalCorrect != 1) throw new RuntimeException("One true option is required");
        problemResponseDto.setTopicName(topic.getName());
        problemResponseDto.setOptions(updatedOptions);
        problemRepo.save(problem);
        return problemResponseDto;
    }


    @Override
    public Map<String, Integer> getDifficultyStats(User user) {
        List<Submission> submissions = submissionRepo.findAllByUser(user);

        Map<String, Integer> difficultyMap = new HashMap<>();
        Set<Long> seenProblems = new HashSet<>();

        for (Submission submission : submissions) {
            if (submission.isCorrect()) {
                Long problemId = submission.getProblem().getProblemId();
                if (!seenProblems.contains(problemId)) {
                    seenProblems.add(problemId);
                    String difficulty = submission.getProblem().getDifficulty().toString();
                    difficultyMap.put(difficulty, difficultyMap.getOrDefault(difficulty, 0) + 1);
                }
            }
        }
        return difficultyMap;
    }

    @Override
    public Map<String, Integer> getTopicStats(User user) {
        List<Submission> submissions = submissionRepo.findAllByUser(user);

        Map<String, Integer> topicMap = new HashMap<>();
        Set<Long> seenProblems = new HashSet<>();

        for (Submission submission : submissions) {
            if (submission.isCorrect()) {
                Long problemId = submission.getProblem().getProblemId();
                if (!seenProblems.contains(problemId)) {
                    seenProblems.add(problemId);
                    String topic = submission.getProblem().getTopic().toString();
                    topicMap.put(topic, topicMap.getOrDefault(topic, 0) + 1);
                }
            }
        }

        return topicMap;
    }

    @Override
    public void deleteProblemById(Long id) {
        Optional<Problem> problemOptional = problemRepo.findById(id);
        if(problemOptional.isEmpty()) throw new ResourceNotFoundException("Problem not found with problem id : " + id);
        Problem problem = problemOptional.get();
        List<ProblemOption> optionForProblem = problemOptionService.getOptionsForDelete(problem);

        for(ProblemOption problemOption : optionForProblem){
            problemOptionRepo.delete(problemOption);
        }
        problemRepo.delete(problem);
    }

    @Override
    public ProblemResponseDto getNextForProblem(Long id) {
        Problem nextProblem = null;
        for(Long next = id+1; next <= id+10; next++){
            Optional<Problem> problemOptional = problemRepo.findById(next);
            if(problemOptional.isPresent()){
                nextProblem = problemOptional.get();
                break;
            }
        }
        if(nextProblem == null){
            nextProblem = problemRepo.findById(id).
                    orElseThrow(() -> new ResourceNotFoundException("Problem with the given problem id not found : " + id.toString()));
        }
        Topic topic = topicRepo.findById(nextProblem.getTopic().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic does not exist for the given problem id : " + id.toString()));

        ProblemResponseDto problemResponseDto = problemResponseDtoMapper.mapTo(nextProblem);
        List<ProblemOptionResponseDto> problemOptionResponseDtos = problemOptionService.getOptionForProblem(nextProblem);

        problemResponseDto.setOptions(problemOptionResponseDtos);
        problemResponseDto.setTopicName(topic.getName());
        problemResponseDto.setCategoryId(nextProblem.getCategory().getId());
        problemResponseDto.setAcceptance((int)((double)nextProblem.getCorrectSubmissions()/(double) nextProblem.getTotalSubmissions())*100);
        return problemResponseDto;
    }

    @Override
    public ProblemResponseDto getPreviousForProblem(Long id) {
        Problem previousProblem = null;

        for(Long prev = id-1; prev >= 0 && prev >= id-10; id--){
            Optional<Problem> problemOptional = problemRepo.findById(prev);
            if(problemOptional.isPresent()){
                previousProblem = problemOptional.get();
                break;
            }
        }
        if(previousProblem == null){
            previousProblem = problemRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Problem with the given problem id not found"));
        }
        Topic topic = topicRepo.findById(previousProblem.getTopic().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic does not exist for the given problem id : "));

        ProblemResponseDto problemResponseDto = problemResponseDtoMapper.mapTo(previousProblem);
        List<ProblemOptionResponseDto> problemOptionResponseDtos = problemOptionService.getOptionForProblem(previousProblem);

        problemResponseDto.setOptions(problemOptionResponseDtos);
        problemResponseDto.setTopicName(topic.getName());
        problemResponseDto.setCategoryId(previousProblem.getCategory().getId());
        problemResponseDto.setAcceptance((int)((double)previousProblem.getCorrectSubmissions()/(double) previousProblem.getTotalSubmissions())*100);
        return problemResponseDto;
    }

    @Override
    public List<ProblemResponseDto> createProblems(List<ProblemRequestDto> problems){
        List<ProblemResponseDto> responseDtos = new ArrayList<>();
        for(ProblemRequestDto problem : problems){
            ProblemResponseDto problemResponseDto = this.createProblem(problem);
            responseDtos.add(problemResponseDto);
        }
        return responseDtos;
    }

    @Override
    public CustomPageResponse<ProblemResponseDto> searchByDifficulty(String difficulty, int page, int size) {
        Sort sort = Sort.by("title").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Problem.Difficulty currDifficult = switch (difficulty) {
            case "EASY" -> Problem.Difficulty.EASY;
            case "MEDIUM" -> Problem.Difficulty.MEDIUM;
            case "HARD" -> Problem.Difficulty.HARD;
            case "EXPERT" -> Problem.Difficulty.EXPERT;
            default -> throw new ResourceNotFoundException("Please enter a valid difficulty");
        };

        Page<Problem> problemsPage = problemRepo.findByDifficulty(currDifficult, pageable);

        Page<ProblemResponseDto> problemResponseDtoPage = problemsPage.map(problem -> {
            ProblemResponseDto problemResponseDto = problemResponseDtoMapper.mapTo(problem);
            List<ProblemOptionResponseDto> optionForProblem = problemOptionService.getOptionForProblem(problem);
            problemResponseDto.setOptions(optionForProblem);
            problemResponseDto.setCategoryId(problem.getCategory().getId());
            problemResponseDto.setTopicName(problem.getTopic().getName());
            problemResponseDto.setAcceptance((int)((double)problem.getCorrectSubmissions()/(double) problem.getTotalSubmissions())*100);            return problemResponseDto;
        });

        CustomPageResponse<ProblemResponseDto> responsePage = new CustomPageResponse<>();

        responsePage.setContent(problemResponseDtoPage.getContent());
        responsePage.setPageNumber(problemResponseDtoPage.getNumber());
        responsePage.setPageSize(problemResponseDtoPage.getSize());
        responsePage.setLast(problemResponseDtoPage.isLast());
        responsePage.setFirst(problemResponseDtoPage.isFirst());
        responsePage.setTotalPages(problemResponseDtoPage.getTotalPages());
        responsePage.setTotalNumberOfElements(problemResponseDtoPage.getTotalElements());
        responsePage.setNumberOfElements(problemResponseDtoPage.getNumberOfElements());

        String email = authUtil.getEmailOfLoggedUser();
        Optional<User> userOptional = userRepo.findByEmail(email);
        userOptional.ifPresent(user -> markSolvedAttemptedUnsolved(responsePage, user));

        return responsePage;
    }

    @Override
    public CustomPageResponse<ProblemResponseDto> searchByTopic(Long topicId, int page, int size) {
        Topic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id : " + topicId.toString()));

        Sort sort = Sort.by("title").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Problem> problemsPage = problemRepo.findByTopic(topic, pageable);
        Page<ProblemResponseDto> problemResponseDtoPage = problemsPage.map(problem -> {
            ProblemResponseDto problemResponseDto = problemResponseDtoMapper.mapTo(problem);
            List<ProblemOptionResponseDto> optionForProblem = problemOptionService.getOptionForProblem(problem);
            problemResponseDto.setOptions(optionForProblem);
            problemResponseDto.setCategoryId(problem.getCategory().getId());
            problemResponseDto.setTopicName(problem.getTopic().getName());
            problemResponseDto.setAcceptance((int)((double)problem.getCorrectSubmissions()/(double) problem.getTotalSubmissions())*100);
            return problemResponseDto;
        });

        CustomPageResponse<ProblemResponseDto> responsePage = new CustomPageResponse<>();

        responsePage.setContent(problemResponseDtoPage.getContent());
        responsePage.setPageNumber(problemResponseDtoPage.getNumber());
        responsePage.setPageSize(problemResponseDtoPage.getSize());
        responsePage.setLast(problemResponseDtoPage.isLast());
        responsePage.setFirst(problemResponseDtoPage.isFirst());
        responsePage.setTotalPages(problemResponseDtoPage.getTotalPages());
        responsePage.setTotalNumberOfElements(problemResponseDtoPage.getTotalElements());
        responsePage.setNumberOfElements(problemResponseDtoPage.getNumberOfElements());

        String email = authUtil.getEmailOfLoggedUser();
        Optional<User> userOptional = userRepo.findByEmail(email);
        userOptional.ifPresent(user -> markSolvedAttemptedUnsolved(responsePage, user));

        return responsePage;
    }

    @Override
    public Integer getTotalProblemsByUser() {
        String email = authUtil.getEmailOfLoggedUser();
        if(email.isBlank() || email.isEmpty()) throw new RuntimeException("Please login to continue");
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found, try logout and loggin in again"));

        Map<String, Integer> difficultyStats = this.getDifficultyStats(user);
        int total = 0;
        for(var category : difficultyStats.keySet()) total += difficultyStats.get(category);
        return total;
    }

    @Override
    public Long getTotalProblems() {
        return problemRepo.count();
    }

    @Override
    public CustomPageResponse<ProblemResponseDto> getByCategory(Long id, int page, int size) {
        Sort sort = Sort.by("problemId");
        Pageable pageable = PageRequest.of(page, size, sort);
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id : " + id));
        Page<Problem> problemsPage = problemRepo.findByCategory(category, pageable);

        Page<ProblemResponseDto> problemResponseDtoPage = problemsPage.map(problem -> {
            ProblemResponseDto problemResponseDto = problemResponseDtoMapper.mapTo(problem);
            List<ProblemOptionResponseDto> optionForProblem = problemOptionService.getOptionForProblem(problem);
            problemResponseDto.setOptions(optionForProblem);
            problemResponseDto.setCategoryId(problem.getCategory().getId());
            problemResponseDto.setTopicName(problem.getTopic().getName());
            problemResponseDto.setAcceptance((int)((double)problem.getCorrectSubmissions()/(double) problem.getTotalSubmissions())*100);
            return problemResponseDto;
        });

        CustomPageResponse<ProblemResponseDto> responsePage = new CustomPageResponse<>();

        responsePage.setContent(problemResponseDtoPage.getContent());
        responsePage.setPageNumber(problemResponseDtoPage.getNumber());
        responsePage.setPageSize(problemResponseDtoPage.getSize());
        responsePage.setLast(problemResponseDtoPage.isLast());
        responsePage.setFirst(problemResponseDtoPage.isFirst());
        responsePage.setTotalPages(problemResponseDtoPage.getTotalPages());
        responsePage.setTotalNumberOfElements(problemResponseDtoPage.getTotalElements());
        responsePage.setNumberOfElements(problemResponseDtoPage.getNumberOfElements());

        String email = authUtil.getEmailOfLoggedUser();
        Optional<User> userOptional = userRepo.findByEmail(email);
//        if(userOptional.isPresent()){
//            markSolvedAttemptedUnsolved(responsePage, userOptional.get());
//        }
//        equivalent to above
        userOptional.ifPresent(user -> markSolvedAttemptedUnsolved(responsePage, user));


        return responsePage;
    }

    @Override
    public CustomPageResponse<ProblemResponseDto> filter(FilterDto filterDto, int page, int size) {
        String email = authUtil.getEmailOfLoggedUser();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Please login to use filter"));

        boolean includeDifficulty = false;
        boolean includeTopic = false;
        Problem.Difficulty currDifficult = Problem.Difficulty.EASY;
        Topic topic = null;

        if(filterDto.getTopicId() != -1){
            includeTopic = true;
            topic = topicRepo.findById(filterDto.getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id : " + filterDto.getTopicId()));
        }

        if(!filterDto.getDifficulty().equalsIgnoreCase("ANY")) {
            includeDifficulty = true;
                currDifficult = switch (filterDto.getDifficulty()) {
                case "EASY" -> Problem.Difficulty.EASY;
                case "MEDIUM" -> Problem.Difficulty.MEDIUM;
                case "HARD" -> Problem.Difficulty.HARD;
                case "EXPERT" -> Problem.Difficulty.EXPERT;
                default -> throw new ResourceNotFoundException("Please enter a valid difficulty");
            };
        }

        Sort sort = Sort.by("problemId");
        Pageable pageable = PageRequest.of(page, size, sort);
        String status = filterDto.getStatus();
        Page<Problem> problemPage;

        if(status.equalsIgnoreCase("ANY")){
            if(includeTopic && includeDifficulty){
                problemPage = problemRepo.findByTopicAndDifficulty(topic, currDifficult, pageable);
            }
            else if(includeTopic){
                problemPage = problemRepo.findByTopic(topic, pageable);
            }
            else problemPage = problemRepo.findByDifficulty(currDifficult, pageable);
        }
        else{
            problemPage = problemRepo.findProblemsByFilters(currDifficult, status, topic, user.getUserId(), pageable);
        }

        CustomPageResponse<ProblemResponseDto> responsePage = new CustomPageResponse<>();

        Page<ProblemResponseDto> problemResponseDtoPage = problemPage.map(problem -> {
           ProblemResponseDto problemResponseDto = problemResponseDtoMapper.mapTo(problem);
           List<ProblemOptionResponseDto> optionForProblem = problemOptionService.getOptionForProblem(problem);
           problemResponseDto.setOptions(optionForProblem);

           problemResponseDto.setCategoryId(problem.getCategory().getId());
           problemResponseDto.setTopicName(problem.getTopic().getName());
           problemResponseDto.setAcceptance((int)((double)problem.getCorrectSubmissions()/(double) problem.getTotalSubmissions())*100);
           return problemResponseDto;
        });

        responsePage.setContent(problemResponseDtoPage.getContent());
        responsePage.setPageNumber(problemResponseDtoPage.getNumber());
        responsePage.setPageSize(problemResponseDtoPage.getSize());
        responsePage.setLast(problemResponseDtoPage.isLast());
        responsePage.setFirst(problemResponseDtoPage.isFirst());
        responsePage.setTotalPages(problemResponseDtoPage.getTotalPages());
        responsePage.setTotalNumberOfElements(problemResponseDtoPage.getTotalElements());
        responsePage.setNumberOfElements(problemResponseDtoPage.getNumberOfElements());

        markSolvedAttemptedUnsolved(responsePage, user);

        return responsePage;
    }

    @Override
    public void markSolvedAttemptedUnsolved(CustomPageResponse<ProblemResponseDto> customPageResponse, User user) {
//        System.out.println("--------------------mark problem running ------------------------------");
        List<Submission> submissions = submissionRepo.findAllByUser(user);

        // true -> solved, false -> attempted, if mp not have key unsolved
        HashMap<Long, Boolean> mp = new HashMap<>();
        for(Submission submission : submissions){
            Long problemId = submission.getProblem().getProblemId();
            if(mp.containsKey(problemId)){
                if(!mp.get(problemId) && submission.isCorrect()) mp.put(problemId, true);
            }
            else{
                mp.put(problemId, submission.isCorrect());
            }
        }
        for(ProblemResponseDto problemResponseDto : customPageResponse.getContent()){
            Long problemId = problemResponseDto.getProblemId();
            if(mp.containsKey(problemId)){
                if(mp.get(problemId)) problemResponseDto.setStatus("solved");
                else problemResponseDto.setStatus("attempted");
            }
            else problemResponseDto.setStatus("unsolved");
        }
    }
}

















