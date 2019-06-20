package plugin;

import hudson.Extension;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.tasks.Publisher;
import hudson.triggers.SCMTrigger;

import javax.annotation.Nonnull;
import java.util.Map;


@Extension
public class JobListener extends RunListener<AbstractBuild> {

    public JobListener() {
        super(AbstractBuild.class);
    }

    @Override
    public void onStarted(AbstractBuild build, TaskListener listener) {
        LarkService service = getService(build, listener);
        if (service == null) {
            return;
        }
        service.start(getDescription(build));
    }

    @Override
    public void onCompleted(AbstractBuild build, @Nonnull TaskListener listener) {
        Result result = build.getResult();
        LarkService service = getService(build, listener);
        if (service == null) {
            return;
        }

        if (result != null && result.equals(Result.SUCCESS)) {
            service.success(getDescription(build));
        } else if (result != null && result.equals(Result.FAILURE)) {
            service.failed();
        } else {
            service.abort();
        }
    }

    private LarkService getService(AbstractBuild build, TaskListener listener) {
        Map<Descriptor<Publisher>, Publisher> map = build.getProject().getPublishersList().toMap();
        for (Publisher publisher : map.values()) {
            if (publisher instanceof LarkNotifier) {
                return ((LarkNotifier) publisher).newLarkService(build, listener);
            }
        }
        return null;
    }

    private String getDescription(AbstractBuild build){
        String description = null;
        CauseAction causeAction = build.getAction(CauseAction.class);
        if (causeAction != null){
            Cause scmCause = causeAction.findCause(SCMTrigger.SCMTriggerCause.class);
            if (scmCause == null){
                description =  causeAction.getCauses().get(0).getShortDescription();
            }
        }
        return description;
    }


}
