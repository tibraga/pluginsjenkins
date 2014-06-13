package br.eti.tiagobraga.jenkins.plugins.viewerhar;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Pattern;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * @author Tiago Braga
 */
public class ViewerHarReportPublisher extends Recorder implements Serializable {

    private static final long serialVersionUID = 28042011L;
    
    private final String testResultsDir;

    /**
     * 
     * @param testResults
     * @stapler-constructor
     */
    @DataBoundConstructor
    public ViewerHarReportPublisher(final String testResultsDir) {
        super();
        this.testResultsDir = testResultsDir;
    }

    public String getTestResultsDir() {
        return testResultsDir;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {    	
        listener.getLogger().println("Publishing Viewer Har report...");
        FilePath harResults = build.getWorkspace().child(this.testResultsDir);
        listener.getLogger().println("Path files: " + harResults.toURI().toString());
        if (!harResults.exists()) {
            listener.getLogger().println("Missing file " + this.testResultsDir);
            return false;
        }        
//        if (seleniumResults.list().isEmpty()) {
//            listener.getLogger().println("Missing selenium result files in directory " + this.testResultsDir);
//            return false;
//        }
        FilePath target = new FilePath(new File(build.getRootDir().getPath() + File.separatorChar + "reporthar.json"));
        copyReports(harResults, target, listener);
        ViewerHarReportAction action = new ViewerHarReportAction(build, listener, build.getRootDir());
        build.getActions().add(action);
        return true;
    }

    private void copyReports(FilePath harResults, FilePath target, BuildListener listener) throws IOException, InterruptedException {
        listener.getLogger().println("Copying the reports.");
        harResults.copyTo(target);
        listener.getLogger().println("File copy to " + target.toURI().toString());
    }


    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public DescriptorImpl() {
            super(ViewerHarReportPublisher.class);
        }

        public String getDisplayName() {
            return "Publish Viewer HAR Report";
        }

        public FormValidation doCheckTestResultsDir(@QueryParameter String value) {
            if (value == null || value.isEmpty()) {
                return FormValidation.error("Missing file har location relative to your workspace");
            }
            if(isAbsolute(value)) {
                return FormValidation.error("Please give a file location relative to your workspace");
            }
            return FormValidation.ok();
        }

        private static boolean isAbsolute(String rel) {
            return rel.startsWith("/") || DRIVE_PATTERN.matcher(rel).matches();
        }

        private static final Pattern DRIVE_PATTERN = Pattern.compile("[A-Za-z]:[\\\\/].*"),
            ABSOLUTE_PREFIX_PATTERN = Pattern.compile("^(\\\\\\\\|(?:[A-Za-z]:)?[\\\\/])[\\\\/]*");

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    } 
}