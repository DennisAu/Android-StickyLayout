# Android-StickyLayout

the Sticky Layout(sl for shot) has 3 parts, which are the main layout and 2 children layouts.

 ![QQ20150828-1@2x](https://raw.githubusercontent.com/DennisAu/Android-StickyLayout/master/wiki-res/QQ20150828-1%402x.png)

As the picture above, the hold pic is the main layout(sl). And the lay1, lay2 are the children layouts.

### What can sl do?

Scroll up the screen after the lay1 reach its bottom, the lay2 will show. And if finger up when lay2's top reach the half screen, lay2 will auto move to the top of the screen.

### Status of the sl

to support the sl's funciton, the children layouts need to return a value:

- lay1: can move up
- lay2: can move down

anchor init status is at bottom.

###### when sl anchor at bottom

| sl anchor at | lay1: can move up | finger move |    sl motion    | 
| :----------: | :---------------: | :---------: | :-------------: | 
|    bottom    |        no         |    `up`     | intercept touch | 
|    bottom    |        no         |    down     |   to children   | 
|    bottom    |        yes        |      -      |   to children   | 

###### when sl anchor at top

| sl anchor at | lay2: can move down | finger move |    sl motion    | 
| :----------: | :-----------------: | :---------: | :-------------: | 
|     top      |         no          |     up      |   to children   | 
|     top      |         no          |   `down`    | intercept touch | 
|     top      |         yes         |      -      |   to children   | 

###### when sl in the middle status

when the sl attempting to anchor top or bottom, the finger down. sl will stop the animation, and go to logic of drag.

| sl anchoring |                sl motion                 | 
| :----------: | :--------------------------------------: | 
|     true     |             intercept touch              | 
|    false     | go to  the anchor at top or bottom logic | 



### About the code design details

\```flow

st=>start: lay1 touch event

e=>end: end, lay1 super touch

opLay1Scroll=>operation: sl scroll to anchor

condLay1CanUp=>condition: can lay1 move up

condFingerMove=>condition: is Finger Move Direction Up

condSlAnchor=>condition: is Sl at bottom

st->condLay1CanUp(no, bottom)->condFingerMove(yes)->condSlAnchor(yes,bottom)->opLay1Scroll

condLay1CanUp(yes)->e

condFingerMove(no)->e

condSlAnchor(no)->e

\```

