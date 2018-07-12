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

favorite_libraries <- c("Matrix", "data.table", "randomForest", "xgboost", "xgboostExplainer", "lightgbm",
                        "vcd", "ggplot2", "scales", "GGally",
                        "mlr", "mlrMBO", "irace",
                        "rstanarm", "rstan",
                        "h2o", "caret", "prophet", "lubridate",
                        "future.apply", "future.callr", "DALEX"
                        )
## require.or.install.packages(favorite_libraries  )


## Attempt to automatically download the data file if it is not present.
try.download.file.unless.it.exists <- function(data_url, filename) {
    if( !file.exists(filename)) {
        try(download.file(data_url, filename))
    }
}
## Usage: try.download.file.unless.it.exists( "your url goes here", "iris.csv")


unzip.file.unless.target.exists <- function(filename_zipped, filename_unzipped) {
    ## Unzip the file, if necessary
    if( !file.exists(filename_unzipped)) {
        try(unzip(filename_zipped,, filename_unzipped))
    }
}


stop.if.file.isnt.there <- function(filename) {
    if( !file.exists(filename)) {
        stop(paste("File", filename,
                   "not found! Please place file", filename,
                   "in the current directory before running this program."))
    }
}



