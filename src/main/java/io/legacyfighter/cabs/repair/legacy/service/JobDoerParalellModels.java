package io.legacyfighter.cabs.repair.legacy.service;

import io.legacyfighter.cabs.party.api.PartyId;
import io.legacyfighter.cabs.repair.api.RepairProcess;
import io.legacyfighter.cabs.repair.api.RepairRequest;
import io.legacyfighter.cabs.repair.api.ResolveResult;
import io.legacyfighter.cabs.repair.legacy.dao.UserDAO;
import io.legacyfighter.cabs.repair.legacy.job.CommonBaseAbstractJob;
import io.legacyfighter.cabs.repair.legacy.job.JobResult;
import io.legacyfighter.cabs.repair.legacy.job.RepairJob;
import io.legacyfighter.cabs.repair.legacy.user.CommonBaseAbstractUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobDoerParalellModels {

    private UserDAO userDAO;

    @Autowired
    private RepairProcess repairProcess;

    public JobDoerParalellModels(UserDAO userDAO){
        this.userDAO = userDAO;  //I'll inject a test double some day because it makes total sense to me
    }

    public JobResult repair(Long userId, CommonBaseAbstractJob job){
        CommonBaseAbstractUser user = userDAO.getOne(userId);
        return user.doJob(job);
    }

    public JobResult repair2parallelModels(Long userId, CommonBaseAbstractJob job){
        //legacy model
        CommonBaseAbstractUser user = userDAO.getOne(userId);
        JobResult jobResult = user.doJob(job);

        //new model
        ResolveResult newResult = runParallelModel(userId, (RepairJob) job);

        compare(newResult, jobResult);

        return jobResult;
    }

    private ResolveResult runParallelModel(Long userId, RepairJob job) {
        PartyId vehicle = findVehicleFor(userId);
        RepairRequest repairRequest = new RepairRequest(vehicle, job.getPartsToRepair());
        return repairProcess.resolve(repairRequest);
    }

    private PartyId findVehicleFor(Long userId) {
        //TODO search in graph
        return new PartyId();
    }

    private void compare(ResolveResult resolveResult, JobResult jobResult){
        assert (resolveResult.getStatus().equals(ResolveResult.Status.SUCCESS)
                &&
               jobResult.getDecision().equals(JobResult.Decision.ACCEPTED))
                ||
                (resolveResult.getStatus().equals(ResolveResult.Status.ERROR)
                && jobResult.getDecision().equals(JobResult.Decision.ERROR));

        //TODO
    }
}
