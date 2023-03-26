package org.niooii.githubUtils;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;

public class GithubTests {


    public GithubTests() throws IOException {
        GitHub git = new GitHubBuilder().withOAuthToken("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").build();
        GHUser user = git.getMyself();
        //GHRepository repo = user.createRepository("yes").create();
        GHRepository repo = user.getRepository("yes");
        repo.setDescription("odl test");
        System.out.println(repo.getHttpTransportUrl());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        gitList list = new gitList();
        if(list.addUser("test35", "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")) System.out.println("added successfully");
        else System.out.println("invalid credentials");
        GitHub con = list.getGitConnection("test35"); // gets github connection from
        GHUser user = con.getMyself();
    }
}
