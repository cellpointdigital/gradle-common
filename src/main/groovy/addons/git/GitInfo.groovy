/**
 * Copyright (c) 2023. Cellpoint Digital
 * www.cellpointdigital.com
 */

package addons.git

/**
 * Gets information from GIT and put it into ext.gitDetails property as instance of GitInfo
 */
class GitInfo {
    String branchName
    String commitId
    String closestTagName
    boolean dirty
    List<String> tags

    GitInfo(String branchName, String commitId, String closestTagName, boolean dirty, List<String> tags) {
        this.branchName = branchName
        this.commitId = commitId
        this.closestTagName = closestTagName
        this.dirty = dirty
        this.tags = dirty ? [] : tags
    }
}
