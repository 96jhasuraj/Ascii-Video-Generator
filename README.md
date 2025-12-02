# Terminal Ascii Video Generator
![3](https://github.com/user-attachments/assets/9dc3116e-4336-4558-82f5-67abd90d5899)
App creates Frames offline 
VideoPlayer reads those frames & runs it on terminal
Idea is to generate a video with ascii characters with colors & brightness adusted according to original videos pixels. I use a producer - consumer pattern for synchronisation.

Current Command : 
java .\App.java .\video.mp4 ./frames 
java VideoPlayer.java -c -d ./frames 34411 -f 144
## TODO
- [ ] Remove coupling between App & VideoPlayer ; there should be only 1
- [ ] Package everything into a jar
- [ ] Currently it first writes frames in ./frames folder. Then VideoPlayer.java reads it . Fix this , integrate the logic in a new thread in VideoPlayer itself
- [ ] release github packages / jar
