package io.legacyfighter.cabs.repair.legacy.user;

import io.legacyfighter.cabs.repair.legacy.job.JobResult;
import io.legacyfighter.cabs.repair.legacy.job.RepairJob;

public class EmployeeDriverWithLeasedCar extends EmployeeDriver{

    private Long lasingCompanyId;

    @Override
    protected JobResult handle(RepairJob job) {
        return new JobResult(JobResult.Decision.REDIRECTION).addParam("shouldHandleBy", lasingCompanyId);
    }

    public Long getLasingCompanyId() {
        return lasingCompanyId;
    }

    public void setLasingCompanyId(Long lasingCompanyId) {
        this.lasingCompanyId = lasingCompanyId;
    }
}
