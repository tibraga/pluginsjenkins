package br.eti.tiagobraga.jenkins.plugins.viewerhar;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.DirectoryBrowserSupport;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * @author Tiago Braga
 */
public class ViewerHarReportAction implements Action, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final AbstractBuild<?, ?> build;
    private final File harReportsDir;
    private final BuildListener listener;
    public ViewerHarReportAction(AbstractBuild<?, ?> build, BuildListener listener, File harReportsDir) {
        super();
        this.build = build;
        this.harReportsDir = harReportsDir;
        this.listener = listener;
    }

    public String getIconFileName() {
        return "/plugin/viewerhar/Images/icon_jenkins.png";
    }

    public String getDisplayName() {
        return "Viewer HAR Report";
    }

    public String getUrlName() {
    	return "harreport";
    }

    public String getRootUrl() {
    	return Jenkins.getInstance().getRootUrl();
    }
    
    public String getUrlParams() {
		return "?url=" + Jenkins.getInstance().getRootUrl() + build.getUrl() + "harreport/reporthar.json";
    }
    
    public AbstractBuild<?, ?>getOwner() {
        return this.build;
    }

    

    public DirectoryBrowserSupport doDynamic(StaplerRequest req, StaplerResponse rsp) {
        if (this.build != null) {
            return new DirectoryBrowserSupport(this, new FilePath(this.harReportsDir),
                    "harreport", "clipboard.gif", false);
        }
        return null;
    }
}