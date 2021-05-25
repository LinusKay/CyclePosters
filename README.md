# CyclePosters

(i am probably going to rename this better at some point)

A plugin that allows you to place any image in game, in the form of a "poster" made up of maps/item frames. These posters can be assigned multiple images, and cycled through manually. Posters can additionally be setup to provide the user with a clickable message linked to  a URL. 

##Commands
#### /poster place \<poster name> \<width> \<height> \<image> [image .. image]
*eg: /poster place events 3 2 welcome.png events/partyevent.png*

place poster of given size at click location.

* poster name: unique name to identify poster
* width: width (in blocks) of poster
* height: height (in blocks) of poster
* image: image name to us. image must be located within plugin/CyclePosters. 

#### /poster next \<poster name>
*eg /poster next events*

manually cycle poster to next slide
* poster name: unique name to identify poster

##Permissions
####cycleposters.manage
required to run *any* commands

##Config Structure
####data.yml
all poster data is stored in data.yml
this contains the name, size and image data. 
```yaml
posters:
    events: # name
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
                click_message: Learn about our welcome event!
                # text to show on chat message hover
                click_hover: Click here to visit website!
                # link to take user to on chat message click
                click_url: https://csitsociety.club/
```

##To-Do
* Implement auto-cycling of poster on specified interval