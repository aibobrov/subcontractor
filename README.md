# Subcontractor

## Table of Contents
- [Subcontractor](#subcontractor)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [TODO](#todo)
  - [Documentation](#documentation)
  - [Authors](#authors)

Slack bot for polling integration in your workspace.
User experience inspired by [Liquid Democracy](https://medium.com/organizer-sandbox/liquid-democracy-true-democracy-for-the-21st-century-7c66f5e53b6f) principle.

## Features

* Poll creation
  * Different poll types: SingleChoice, Argree/Disagree, 1-to-n.
  * Make poll anonymous.
  * Disable showing results of the poll.
  * Scheduling the poll (_beta_)
* Liquid Democracy
  * Delegate your vote to another person in workspace
* Poll's tag system for static delegation to selected person
* Subcontractor's core is totally independent from Slack. There is to possibility to add/change another platform.
  
## TODO

- [ ] Add some more poll types: MultipleChoice, NPS, Yes/No.
- [ ] Add option for user to add his own vote choices
- [ ] Change database

## Documentation

For Get Started Guide and some additional documentation see [`./docs`](./docs) folder.

## Authors

* [@artbobrov](https://github.com/artbobrov)
* [@Liza858](https://github.com/Liza858)