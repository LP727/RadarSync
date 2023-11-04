# RadarSync

## Table of Contents
- [Spec bot](#spec-bot)
  - [Table of Contents](#table-of-contents)
  - [About](#about)
  - [Usage](#usage)
  - [Licence](#licence)

## About
After purchasing my first e-bike, I couldn't help getting worried about it getting stolen and I thought to myself: "Why not install a tracker on it?". That why if my bike suddenly starts moving by itself, I'll know!
Fast forward a couple of month, I had a tracker that would update my bike's position to my server at home, but I found it quite inconvenient to use Termux or a browser on my phone to fetch it's position and I figured it would be much more convenient to have an app do the polling and informing for me.

And thus was born the RadarSync idea, an app that would keep me updated with the position of my bike, car, ... or pretty much any valuable that I choose to track!

Disclamer, I am NOT a professional Kotlin/Android developper, this project was developped as a tool for my own convenience and it is NOT deploy ready in any shape or form.
To have efficient tracking of anything, you need to have an internet connection so it can get quite expensive to track many thing, hence the purpose to track valuables or things that are already connected (like a car with wifi service per example).

## Usage
This repository only contains the app itself, I might one day upload the design document for the tracker circuite firmware and hardware as the server but so far this will require user to generate their own server/tracker to work with this.

To use the app, one must either have their server's certificate signed by a recognized authority or place their .cert file in the <code>/app/src/main/res/raw/</code> path
Then the user must enter their server's path to the positions data, port and credentials in the settings page of the app.

## Licence
TODO: Insert a license if the project ever goes somewhere
