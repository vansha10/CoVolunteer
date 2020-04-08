# CoVolunteer

You can download the app from here:  
https://drive.google.com/file/d/1mIQagq6YCeqRbovEp6HDd9muBx39KQ5r/view?usp=sharing

# Getting Started
In order to contribute to the project, you need to follow the steps below:  
(You can skip steps 3 and 4 if you won't be testing the 'Add a request' feature)
1. Download and setup Android Studio.
2. Fork this repository and clone it.
3. Generate a google maps api key
    1. Follow the [Get an API Key guide](https://developers.google.com/places/android-sdk/get-api-key) to get an API key
    2. [Enable billing](https://console.cloud.google.com/project/_/billing/enable?redirect=https://developers.google.com/places/android-sdk/start?dialogOnLoad%3Dbilling-enabled) on your project.(You won't be charged)
    3. Enable the [Places API](https://cloud.google.com/console/apis/library/places-backend.googleapis.com) for your project.
4. Replace "YOUR_API_KEY" in _gradle.properties_ with the key generated in the above step.
5. All set! Build the project and use the log in credentials below to log in.


**Demo Login Credentials:**

* For volunteer profile:  
email: demo1@covolunteer.com  
password: 12345678
* For people who need help profile:  
email: demo2@covolunteer.com  
password: 12345678

# Contributing Guidelines
* Use the [issue tracker](https://github.com/vansha10/CoVolunteer/issues) here on GitHub to file bugs or feature requests.
* Before starting to work on an issue, ask to be assigned to that issue. Make sure that it is not already assigned to some one else.
* When working on issues make sure that you are working on your own fork of the repository.
* Create a new branch for every issue with issue name or number as branch name.
* Before making a pull request, run `git pull origin <branch_name>` to pull changes from repository.
* Make sure to [squash your commits](https://stackoverflow.com/questions/5189560/squash-my-last-x-commits-together-using-git) into a single commit
* Ensure good code style and practices.
