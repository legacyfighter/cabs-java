package io.legacyfighter.cabs.repair.legacy.service;

import io.legacyfighter.cabs.repair.legacy.dao.UserDAO;
import io.legacyfighter.cabs.repair.legacy.job.CommonBaseAbstractJob;
import io.legacyfighter.cabs.repair.legacy.job.JobResult;
import io.legacyfighter.cabs.repair.legacy.user.CommonBaseAbstractUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobDoer {

    private UserDAO userDAO;

    public JobDoer(UserDAO userDAO){
        this.userDAO = userDAO;  //I'll inject test double some day because it makes total sense to me
    }

    public JobResult repair(Long userId, CommonBaseAbstractJob job){
        CommonBaseAbstractUser user = userDAO.getOne(userId);
        return user.doJob(job);
    }

    public JobResult repair2parallelModels(Long userId, CommonBaseAbstractJob job){
        CommonBaseAbstractUser user = userDAO.getOne(userId);
        return user.doJob(job);
    }
}
