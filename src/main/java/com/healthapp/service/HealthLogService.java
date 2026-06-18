package com.healthapp.service;

import com.healthapp.entity.HealthLog;
import com.healthapp.repository.HealthLogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HealthLogService {

    private static final double SLEEP_THRESHOLD = 6.0;
    private static final int STEPS_THRESHOLD = 5000;
    private static final int MOOD_THRESHOLD = 5;

    private final HealthLogRepository healthLogRepository;

    public HealthLogService(HealthLogRepository healthLogRepository) {
        this.healthLogRepository = healthLogRepository;
    }

    public List<HealthLog> findAll() {
        return healthLogRepository.findAll();
    }

    public HealthLog create(HealthLog healthLog) {
        healthLog.setRiskLevel(assessRisk(healthLog));
        return healthLogRepository.save(healthLog);
    }

    public HealthLog update(Long id, HealthLog updatedLog) {
        HealthLog existing = findByIdOrThrow(id);
        existing.setLogDate(updatedLog.getLogDate());
        existing.setSleepHours(updatedLog.getSleepHours());
        existing.setSteps(updatedLog.getSteps());
        existing.setMoodScore(updatedLog.getMoodScore());
        existing.setRiskLevel(assessRisk(existing));
        return healthLogRepository.save(existing);
    }

    public void delete(Long id) {
        HealthLog existing = findByIdOrThrow(id);
        healthLogRepository.delete(existing);
    }

    public Map<String, String> assessLatestRisk() {
        HealthLog latest = healthLogRepository.findTopByOrderByLogDateDescIdDesc()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "尚無健康日誌資料可供評估"));
        String riskLevel = assessRisk(latest);
        latest.setRiskLevel(riskLevel);
        healthLogRepository.save(latest);
        return Map.of("riskLevel", riskLevel);
    }

    // ── 多層決策樹：第一層 sleep → 第二層 steps → 第三層 mood ──

    public String assessRisk(HealthLog healthLog) {
        return assessBySleep(healthLog.getSleepHours(), healthLog.getSteps(), healthLog.getMoodScore());
    }

    /** 第一層：依睡眠時數分支 */
    private String assessBySleep(double sleepHours, int steps, int moodScore) {
        if (sleepHours < SLEEP_THRESHOLD) {
            return assessByStepsWhenSleepInsufficient(steps, moodScore);
        }
        return assessByStepsWhenSleepSufficient(steps, moodScore);
    }

    /** 第二層（睡眠不足）：依步數分支 */
    private String assessByStepsWhenSleepInsufficient(int steps, int moodScore) {
        if (steps < STEPS_THRESHOLD) {
            return assessByMoodWhenSleepAndStepsInsufficient(moodScore);
        }
        return assessByMoodWhenOnlySleepInsufficient(moodScore);
    }

    /** 第二層（睡眠充足）：依步數分支 */
    private String assessByStepsWhenSleepSufficient(int steps, int moodScore) {
        if (steps < STEPS_THRESHOLD) {
            return assessByMoodWhenOnlyStepsInsufficient(moodScore);
        }
        return assessByMoodWhenSleepAndStepsSufficient(moodScore);
    }

    /** 第三層：睡眠不足 + 活動不足 */
    private String assessByMoodWhenSleepAndStepsInsufficient(int moodScore) {
        if (moodScore < MOOD_THRESHOLD) {
            return "高風險";
        }
        return "中風險";
    }

    /** 第三層：僅睡眠不足 */
    private String assessByMoodWhenOnlySleepInsufficient(int moodScore) {
        if (moodScore < MOOD_THRESHOLD) {
            return "中風險";
        }
        return "低風險";
    }

    /** 第三層：僅活動不足 */
    private String assessByMoodWhenOnlyStepsInsufficient(int moodScore) {
        if (moodScore < MOOD_THRESHOLD) {
            return "中風險";
        }
        return "低風險";
    }

    /** 第三層：睡眠與活動皆充足 */
    private String assessByMoodWhenSleepAndStepsSufficient(int moodScore) {
        if (moodScore < MOOD_THRESHOLD) {
            return "低風險";
        }
        return "低風險";
    }

    private HealthLog findByIdOrThrow(Long id) {
        return healthLogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "找不到 ID 為 " + id + " 的健康日誌"));
    }
}
