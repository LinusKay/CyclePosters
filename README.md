# CyclePosters

![Image of plugin in action. A 9x6 wall of maps displays a number of cycling images](https://media0.giphy.com/media/CwNQl9X9l7DRhNj3lg/giphy.gif?cid=790b761145b82b9ade04a9ddf83e11967d1708eb87dcb67d&rid=giphy.gif&ct=g)

*PLEASE NOTE that this plugin is currently in early stages, and contains a number of unchecked errors and major performance issues. It is not presently recommended for production servers, as you WILL experience issues. The plugin will also likely undergo a name change in the coming future.*


A plugin that allows you to place any image in game, in the form of a "poster" made up of maps/item frames. These
posters can be assigned multiple images, which can cycle on a specified interval. Posters can additionally be setup to
provide the user with a clickable message linked to a URL. 



## Commands

**/poster place \<poster name> \<width> \<height> \<image> [image .. image]** - *eg: /poster place events 3 2 welcome.png events/partyevent.png*

place poster of given size at click location. if more than one image is given, poster will automatically be set to cycle
between slides every 10 seconds. if only one image is provided, cycling will be disabled. These can be edited in
data.yml. *Newly created posters will only begin their automatic cycle upon server restart.*

* poster name: unique name to identify poster
* width: width (in blocks) of poster
* height: height (in blocks) of poster
* image: image name to us. image must be located within plugin/CyclePosters.

**/poster next \<poster name>** - *eg /poster next events*

manually cycle poster to next slide

* poster name: unique name to identify poster

## Permissions

**cycleposters.manage** - Required to run *any* commands

## Config Structure

#### data.yml

All poster data is stored in data.yml this contains the name, size and image data. A number of custom settings can also be applied to each slide, which determine how the player can interact with the poster. With these additional settings you can:
* send the player a message in chat, which may allow clicking to visit a set URL
* teleport the player to a specific location
* give the player an item
* run a command, either as the player or console

```yaml
posters:
  # unique name of poster
  # REQUIREd
  events:
    #width and height (in blocks) of poster
    # REQUIRED
    width: 3
    height: 2
    # IDs of all map objects belonging to poster
    # REQUIRED
    maps:
      - 39
      - 40
      - 41
      - 42
      - 43
      - 44
    # individual slides of poster
    # poster must include at least one slide, with no limit on the total amount
    # REQUIRED
    slides:
      # REQUIRED: at least one
      slide_0:
        # local file location of slide image
        # images must be located somewhere within plugins/CyclePosters
        # REQUIRED: image will be black otherwise
        image: plugins\CyclePosters/welcome.png
        # message to show in chat upon click
        # messages can have both/either/neither of hover/URL 
        # format messages using § formatting codes
        # eg: §6 = red, §l = bold
        # OPTIONAL
        click_message: Learn about our cool event!
        # text to show on chat message hover
        # OPTIONAL
        # DEPENDS ON click_message
        click_hover: Click here to visit our website!
        # OPTIONAL
        # DEPENDS ON click_message
        # link to take user to on chat message click
        click_url: https://google.com/        
        # location to teleport user
        # first item must match name of existing world
        # following three are X Y Z respectively
        # OPTIONAL
        teleport:
          - "world"
          - 2500
          - 100
          - -1739
        # if true, player will be teleported to ground/highest block at location
        # disable if not concerned about dangerous teleport location
        # OPTIONAL
        # DEPENDS ON teleport
        teleport_safely: true
        # give item to user on click
        # name/lore are optional
        # OPTIONAL
        click_item: STONE
        # custom name to give to item
        # OPTIONAL
        # DEPENDS ON click_item
        click_item_name: §6Cool stone
        # lore/item description to give to item
        # OPTIONAL
        # DEPENDS ON click_item
        click_item_lore:
          - §7This is a really cool item
          - §7Like. wow. amazing.
        # run command on click
        # if run_command_as_console is true, command will be run by console
        # otherwise, if false or not set, command will be run by player that clicked
        # run_command accepts a number of placeholder values
        #   {player} = name of the player that clicked
        # OPTIONAL
        run_command: op {player}
        # set whether command run by player or console
        # OPTIONAL
        # DEPENDS ON run_command
        run_command_as_console: false
    # currently displayed slide
    # will change whenever slide is updated
    # REQUIRED
    current_slide_index: 0
    # time interval (in seconds) between automatic slide changes
    # auto set to 10 when multiple images are provided. 
    # single image posters will be auto set to 0.
    # if not set or set to 0, poster will not auto-cycle to next image
    # OPTIONAL
    interval: 10
```

## To-Do

* Add checks for existing images/posters
* Re-factor for improved performance
* Add function for deleting entire poster, to save on arduous manual labour
* Allow poster slides to accept URLs, as well as local files
* Allow interactions to be locked behind permission nodes
* Allow slide interactions to perform different actions depending on player permissions
* Improve customisability of give item on click feature
