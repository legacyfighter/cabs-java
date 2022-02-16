package io.legacyfighter.cabs.repair.legacy.user;

import io.legacyfighter.cabs.common.BaseEntity;
import io.legacyfighter.cabs.repair.legacy.job.CommonBaseAbstractJob;
import io.legacyfighter.cabs.repair.legacy.job.JobResult;
import io.legacyfighter.cabs.repair.legacy.job.MaintenanceJob;
import io.legacyfighter.cabs.repair.legacy.job.RepairJob;

public abstract class CommonBaseAbstractUser extends BaseEntity {
    public JobResult doJob(CommonBaseAbstractJob job){
        //poor man's pattern matching
        if (job instanceof RepairJob){
            return handle((RepairJob) job);
        }
        if (job instanceof MaintenanceJob){
            return handle((MaintenanceJob) job);
        }
        return defaultHandler(job);
    }

    protected JobResult handle(RepairJob job) {
        return defaultHandler(job);
    }

    protected JobResult handle(MaintenanceJob job) {
        return defaultHandler(job);
    }

    protected JobResult defaultHandler(CommonBaseAbstractJob job){
        throw new IllegalArgumentException(getClass().getName() + " can not handle " + job.getClass().getName());
    }
}
