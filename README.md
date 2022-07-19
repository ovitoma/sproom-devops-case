# Recruitment assignment for Sproom Site Reliability Engineer

This task is intended for candidates applying for the SRE position in the Visma Sproom team. Main purpose of the assignment, is to see how you work with SRE technologies.

We're super happy that you're considering to join us in Sproom, the challenge below should serve as an entrypoint for a good discussion at the technical interview.

## Introduction

Sproom is running one main application that serves our customers. Currently we have a CI/CD pipeline based on AppVeyour, Spinnaker, Packer and Chef solo deploying to Google Cloud Computing VM instances. We would however like to modernize this, and are therefore looking for someone with the skills to help us set up a modern CI/CD pipeline for our service, maintain it, improve our observability, and in time maybe also carve out a separate service or two.

To verify how well a given candidate fit our needs, the test is built with the intention for you to show of your skills in the DevOps/SRE area. We are not very focused on specific technologies, but more on you ability to learn and apply relevant tools, and explain what and why you did it.

Should you feel like expanding above and beyond the scope of the test, feel free to do so. We will enjoy discussing your reasoning for doing so.

## The Challenge

Develop a service that:

* Takes HTTP GET requests and randomly return one of the files in `dummy-pdf-or-png` subdirectory of this repository with the correct mime-type. (note that one of the files is corrupt)
* Provides an endpoint for health monitoring.
* Has a specification how to automatically deploy it
* Has tests, so regressions can be identified.
* There is CI/CD (GitLab, GitHub Actions or similar) to build, test and deploy
* Failing Safe is a priority.
* The service should log relevant information

Overall the service must be considered production-ready.

Bonus points if  
* Metrics (for example Prometheus) are provided from the service.
* Resources are provisioned via IaC
* There is a good developer experience

## Delivery

Fork this repository into a public repository on your own Github profile, and deliver your solution there.

## Questions?

If you have questions about the task or would like us to further specify some of the things written above, you can contact the person who gave you the assignment.