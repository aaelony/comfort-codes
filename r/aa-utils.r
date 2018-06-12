## handy utilities when using R


## Disable `_` to `<-` transition in ESS r-mode 
## (ess-toggle-underscore nil)

## Function to set the printing width to the size of your terminal window
## by relying on `tput` from linux or mac os x
auto.set.terminal.width <- function() {options(width= as.numeric(system("tput cols", intern=T)) -1 )}
## Usage: auto.set.terminal.width()


## Ensure a Recent version of R
recent.r.version <- function(min.version) {
    try( if(sessionInfo()$R.version$major < min.version ) {
             stop(paste("This program requires R version",
                         min.version,
                        "or higher. Please install a recent version of R before continuing. "))
         })
    }
## Usage: recent.r.version(3)        


## Declare a list of packages that need to be installed.
require.or.install.packages <- function(list.of.packages) {

    new.packages <- list.of.packages[!(list.of.packages %in% installed.packages()[,"Package"])]
    if(length(new.packages)) install.packages(new.packages, dependencies=TRUE)
    
    ## Load R package dependencies into the current namespace.
    sapply(list.of.packages, require, character.only=T)

    }
## require.or.install.packages( c("Matrix", "data.table", "xgboost", "lightgbm", "vcd", "ggplot2", "scales", "GGally") )