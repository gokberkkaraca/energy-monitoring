# energy-monitoring
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4ff345d4e93c4e12976cddefb10d1654)](https://www.codacy.com/app/gskaraca1905/energy-monitoring?utm_source=github.com&utm_medium=referral&utm_content=gokberkkaraca/energy-monitoring&utm_campaign=badger)

### Assigned People:
* Satir Osman Berk, Karaca Sabit Gokberk (3.C.1)
* Denir Tudor, Milica Novakovic (3.C.2)

### Objectives:
Development of a full system able to compute the full energy expenditure of people
(i.e., amount of calories spent), both during day and night, via the deployment of an application that
tracks both the energy expenditure due to activity during the day, and the quality of sleeping during
the night. The project will be in two phases: 1) a first phase, individual for each group, and 2) an
integration phase (lead by Group 3.C.2) where both groups will collaborate on the creation of the
full app. 

# Minimum Requirements
## day-monitoring
- [x] Take accelerometer and heartrate (HR) on the watch during day and night, and send it to the
tablet. Synchronize data to the cloud.

- [x] Detection of abrupt changes on the accelerometer signal on the watch (particular numbers
will be given to the student), which triggers a notification, and store that as an event that is
sent to the tablet.

- [x] Detection of increase on HR (above 100 beats per minute), the duration of the HR increase,
and the timestamps of begin-end.

- [x] Visualize on a time plot on the tablet, the accelerometer traces together with the events
associated to changes on the accelerometer signal or heartrate.

- [x] Synchronizing the gathered data with the physical activity, by gathering information about
workouts (you can use external application, like Android Health app, or develop your own
tracking system). 

## night-monitoring

- [x] Usage of a Polar belt to gather RR intervals, as well as collection of heartrate from
smartwatch and accelerometer data.

- [x] Development of a UI on the able to visualize all the data during the night (RR, HR and
accelerometer throughout the night).

- [x] Development of simple algorithm on the tablet and/or watch to process data and track the
different types of sleep: light/deep or whether the subject is awake.

- [x] Development of UI that computes the total calories spent during the night, and that
correlates sleep quality with the physical activity and diet followed on the previous day

## energy-monitoring
- [x] Development of an integrated full day/night energy monitoring application that provides the
number of calories burnt by the subject, and how far was he from the target calories. (Lead by group 3.C.2)

- [x] UI where the user can input the requested calories to be burned daily, together with the diet
followed that day, so that the application can compute the difference. 
