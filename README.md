# CyclePosters

(i am probably going to rename this better at some point)

A plugin that allows you to place any image in game, in the form of a "poster" made up of maps/item frames. These
posters can be assigned multiple images, which can cycle on a specified interval. Posters can additionally be setup to
provide the user with a clickable message linked to a URL.

NOTE: newly created posters will only begin their automatic cycle upon server restart.

## Commands

#### /poster place \<poster name> \<width> \<height> \<image> [image .. image]

*eg: /poster place events 3 2 welcome.png events/partyevent.png*

place poster of given size at click location. if more than one image is given, poster will automatically be set to cycle
between slides every 10 seconds. if only one image is provided, cycling will be disabled. These can be edited in
data.yml.

* poster name: unique name to identify poster
* width: width (in blocks) of poster
* height: height (in blocks) of poster
* image: image name to us. image must be located within plugin/CyclePosters.

#### /poster next \<poster name>

*eg /poster next events*

manually cycle poster to next slide

* poster name: unique name to identify poster

## Permissions

#### cycleposters.manage

required to run *any* commands

## Config Structure

#### data.yml

all poster data is stored in data.yml this contains the name, size and image data.

```yaml
posters:
  # unique name of poster
  events:
    #width and height (in blocks) of poster
    width: 3
    height: 2
    # IDs of all map objects belonging to poster
    maps:
      - 39
      - 40
      - 41
      - 42
      - 43
      - 44
    # individual slides of poster
    # poster must include at least one slide, with no limit on the total amount
    slides:
      slide_0:
        # local file location of slide image
        # images must be located somewhere within plugins/CyclePosters
        image: plugins\CyclePosters/welcome.png
        # message to show in chat upon click
        # messages can have both/either/neither of hover/URL 
        click_message: Learn about our cool event!
        # text to show on chat message hover
        click_hover: Click here to visit our website!
        # link to take user to on chat message click
        click_url: https://google.com/
        # if true, player will be teleported to ground/highest block at location
        # disable if not concerned about dangerous teleport location
        teleport_safely: true
        # location to teleport user
        # first item must match name of existing world
        # following three are X Y Z respectively
        teleport:
          - "world"
          - 2500
          - 100
          - -1739
        # give item to user on click
        # name/lore are optional
        click_item: STONE
        click_item_name: §6Cool stone
        click_item_lore:
          - §7This is a really cool item
          - §7Like. wow. amazing.
        # run command on click
        # if run_command_as_console is true, command will be run by console
        # otherwise, if false or not set, command will be run by player that clicked
        # run_command accepts a number of placeholder values
        #   {player} = name of the player that clicked
        run_command_as_console: false
        run_command: op {player}
    # currently displayed slide
    # will change whenever slide is updated
    current_slide_index: 0
    # time interval (in seconds) between automatic slide changes
    # auto set to 10 when multiple images are provided. 
    # single image posters will be auto set to 0, meaning no cycle will take place.
    interval: 10
```

## To-Do

* Add checks for existing images/posters
* Re-factor for improved performance
* Add customisable plugin messages
* Add colours/formatting in click messages
* Add function for deleting entire poster, to save on arduous manual labour
