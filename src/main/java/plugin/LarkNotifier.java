package plugin;


import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;


public class LarkNotifier extends Notifier {

    private boolean onStart;

    private boolean onSuccess;

    private boolean onFailed;

    private boolean onAbort;


    private String sendUrlList;

    public String getSendUrlList() {
        return sendUrlList;
    }

    public boolean isOnStart() {
        return onStart;
    }

    public boolean isOnSuccess() {
        return onSuccess;
    }

    public boolean isOnFailed() {
        return onFailed;
    }

    public boolean isOnAbort() {
        return onAbort;
    }


    @DataBoundConstructor
    public LarkNotifier(boolean onStart, boolean onSuccess, boolean onFailed, boolean onAbort, String sendUrlList) {
        super();

        this.onStart = onStart;
        this.onSuccess = onSuccess;
        this.onFailed = onFailed;
        this.onAbort = onAbort;
        this.sendUrlList = sendUrlList;
    }

    public LarkService newLarkService(AbstractBuild build, TaskListener listener) {
        return new LarkServiceImpl(sendUrlList, onStart, onSuccess, onFailed, onAbort, listener, build);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        return true;
    }


    @Override
    public LarkNotifierDescriptor getDescriptor() {
        return (LarkNotifierDescriptor) super.getDescriptor();
    }

    @Extension
    public static class LarkNotifierDescriptor extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Lark Jenkins Plugin";
        }
    }
}
