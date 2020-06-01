# Chappie

Chappie is a question answering app which enables users to ask question about virtually anything (related to the data that has been fed to the machine learning model)

## Working

- The app lets user ask a question either by voice (with the help of Google's TextToSpeech) or by manually typing it. 
- Then it sends the question to the server via an HTTP request.
- Server then processes it and returns the answer in JSON.
- App then parses it and shows it on the screen and plays it back  .


## Acknowledgements

- Server side component by [007akshitsaxena](https://github.com/007akshitsaxena/Chappie)
