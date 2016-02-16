#EclipseCVSKeywords

Eclipse plugin that expands keywords on save like CVS does.

Currently implemented tokens: $Id$ expands to $Id: {filename}, {date} {time} {user.name}$

To be done:

    handle file save on Eclipse close
    add extension point allowing custom tokens expanders to be implemented
