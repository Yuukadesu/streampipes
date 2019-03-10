#!/bin/bash

# ARG_OPTIONAL_SINGLE([hostname],[],[Set the default hostname of your server by providing the IP or DNS name],[])
# ARG_OPTIONAL_BOOLEAN([defaultip],[d],[When set the first ip is used as default])
# ARG_OPTIONAL_BOOLEAN([all],[a],[Select all available StreamPipes services])
# ARG_POSITIONAL_MULTI([operation],[The StreamPipes operation (operation-name) (service-name (optional))],[3],[],[])
# ARG_TYPE_GROUP_SET([operation],[type string],[operation],[start,stop,restart,update,set-template,log,list-available,list-active,list-templates,activate,add,deactivate,clean,remove-settings,set-env,unset-env,create-compose,set-version])
# ARG_DEFAULTS_POS()
# ARG_HELP([This script provides advanced features to run StreamPipes on your server])
# ARG_VERSION([echo This is the StreamPipes dev installer v0.1])
# ARGBASH_SET_INDENT([  ])
# ARGBASH_GO()
# needed because of Argbash --> m4_ignore([
### START OF CODE GENERATED BY Argbash v2.7.1 one line above ###
# Argbash is a bash code generator used to get arguments parsing right.
# Argbash is FREE SOFTWARE, see https://argbash.io for more info


die()
{
  local _ret=$2
  test -n "$_ret" || _ret=1
  test "$_PRINT_HELP" = yes && print_help >&2
  echo "$1" >&2
  exit ${_ret}
}

# validators

operation()
{
	local _allowed=("start" "stop" "restart" "update" "set-template" "log" "list-available" "list-active" "list-templates" "activate" "add" "deactivate" "clean" "remove-settings" "set-env" "unset-env" "create-compose" "set-version") _seeking="$1"
	for element in "${_allowed[@]}"
	do
		test "$element" = "$_seeking" && echo "$element" && return 0
	done
	die "Value '$_seeking' (of argument '$2') doesn't match the list of allowed values: 'start', 'stop', 'restart', 'update', 'set-template', 'log', 'list-available', 'list-active', 'list-templates', 'activate', 'add', 'deactivate', 'clean', 'remove-settings', 'set-env', 'unset-env', 'create-compose' and 'set-version'" 4
}


begins_with_short_option()
{
  local first_option all_short_options='dahv'
  first_option="${1:0:1}"
  test "$all_short_options" = "${all_short_options/$first_option/}" && return 1 || return 0
}

# THE DEFAULTS INITIALIZATION - POSITIONALS
_positionals=()
_arg_operation=('' )
# THE DEFAULTS INITIALIZATION - OPTIONALS
_arg_hostname=
_arg_defaultip="off"
_arg_all="off"


print_help()
{
  printf '%s\n' "This script provides advanced features to run StreamPipes on your server"
  printf 'Usage: %s [--hostname <arg>] [-d|--(no-)defaultip] [-a|--(no-)all] [-h|--help] [-v|--version] <operation-1> [<operation-2>] [<operation-3>]\n' "$0"
  printf '\t%s\n' "<operation>: The StreamPipes operation (operation-name) (service-name (optional)) (defaults for <operation-2> to <operation-3> respectively: '' and '')"
  printf '\t%s\n' "--hostname: Set the default hostname of your server by providing the IP or DNS name (no default)"
  printf '\t%s\n' "-d, --defaultip, --no-defaultip: When set the first ip is used as default (off by default)"
  printf '\t%s\n' "-a, --all, --no-all: Select all available StreamPipes services (off by default)"
  printf '\t%s\n' "-h, --help: Prints help"
  printf '\t%s\n' "-v, --version: Prints version"
}


parse_commandline()
{
  _positionals_count=0
  while test $# -gt 0
  do
    _key="$1"
    case "$_key" in
      --hostname)
        test $# -lt 2 && die "Missing value for the optional argument '$_key'." 1
        _arg_hostname="$2"
        shift
        ;;
      --hostname=*)
        _arg_hostname="${_key##--hostname=}"
        ;;
      -d|--no-defaultip|--defaultip)
        _arg_defaultip="on"
        test "${1:0:5}" = "--no-" && _arg_defaultip="off"
        ;;
      -d*)
        _arg_defaultip="on"
        _next="${_key##-d}"
        if test -n "$_next" -a "$_next" != "$_key"
        then
          begins_with_short_option "$_next" && shift && set -- "-d" "-${_next}" "$@" || die "The short option '$_key' can't be decomposed to ${_key:0:2} and -${_key:2}, because ${_key:0:2} doesn't accept value and '-${_key:2:1}' doesn't correspond to a short option."
        fi
        ;;
      -a|--no-all|--all)
        _arg_all="on"
        test "${1:0:5}" = "--no-" && _arg_all="off"
        ;;
      -a*)
        _arg_all="on"
        _next="${_key##-a}"
        if test -n "$_next" -a "$_next" != "$_key"
        then
          begins_with_short_option "$_next" && shift && set -- "-a" "-${_next}" "$@" || die "The short option '$_key' can't be decomposed to ${_key:0:2} and -${_key:2}, because ${_key:0:2} doesn't accept value and '-${_key:2:1}' doesn't correspond to a short option."
        fi
        ;;
      -h|--help)
        print_help
        exit 0
        ;;
      -h*)
        print_help
        exit 0
        ;;
      -v|--version)
        echo This is the StreamPipes dev installer v0.1
        exit 0
        ;;
      -v*)
        echo This is the StreamPipes dev installer v0.1
        exit 0
        ;;
      *)
        _last_positional="$1"
        _positionals+=("$_last_positional")
        _positionals_count=$((_positionals_count + 1))
        ;;
    esac
    shift
  done
}


handle_passed_args_count()
{
  local _required_args_string="'operation'"
  test "${_positionals_count}" -ge 1 || _PRINT_HELP=yes die "FATAL ERROR: Not enough positional arguments - we require between 1 and 3 (namely: $_required_args_string), but got only ${_positionals_count}." 1
  test "${_positionals_count}" -le 3 || _PRINT_HELP=yes die "FATAL ERROR: There were spurious positional arguments --- we expect between 1 and 3 (namely: $_required_args_string), but got ${_positionals_count} (the last one was: '${_last_positional}')." 1
}


assign_positional_args()
{
  local _positional_name _shift_for=$1
  _positional_names="_arg_operation[0] _arg_operation[1] _arg_operation[2] "

  shift "$_shift_for"
  for _positional_name in ${_positional_names}
  do
    test $# -gt 0 || break
    eval "$_positional_name=\${1}" || die "Error during argument parsing, possibly an Argbash bug." 1
    shift
  done
}

parse_commandline "$@"
handle_passed_args_count
assign_positional_args 1 "${_positionals[@]}"

# OTHER STUFF GENERATED BY Argbash
# Validation of values
_arg_operation="$(operation "$_arg_operation" "operation")" || exit 1

### END OF CODE GENERATED BY Argbash (sortof) ### ])
# [ <-- needed because of Argbash


run() {

#	if [ $_arg_logs = "on" ];
#	then
		$1
#	else
#		$1 > /dev/null 2>&1
#	fi
}

endEcho() {
	echo ''
	echo $1
}

getIp() {
    if [ -x "$(command -v ifconfig)" ]; then
        rawip=$(ifconfig | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1')
    elif [ -x "$(command -v ipconfig)" ]; then
        rawip=$(ipconfig | grep -Eo 'IPv4.*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1')
    fi

    rawip=`echo $rawip | sed 's/(%s)*\n/ /g'`
    IFS=' ' declare -a 'allips=($rawip)'

    allips+=( 'Enter IP manually' )

    # if default selected do not show prompt

    if [ $_arg_hostname ] ;
    then
        ip=$_arg_hostname
        echo 'Default IP was selected: '${ip}
    else

				if [ $_arg_defaultip = "on" ];
				then
        	ip=${allips[0]}
        	echo 'Default IP was selected: '${ip}
				else
					echo ''
					echo 'Please select your IP address or add one manually: '
					PS3='Select option: '
					select opt in "${allips[@]}"
					do
							if [ -z "${opt}" ];
							then
									echo "Wrong input, select one of the options";
							else
									ip="$opt"

									if [ "$opt" == "Enter IP manually" ];
									then
											read -p "Enter Ip: " ip
									fi
									break
							fi
        	done
				fi
    fi

}

createCompose() {
  IFS=''

	result=$(head -n 28 docker-compose.yml)

	while read service; do
  	result=$result"\n \n"$(tail -n +3 "services/$service/docker-compose.yml")
	done <system

	result=$result"\n \n"$(tail -n -13 docker-compose.yml)

	echo -e "$result" > "docker-compose.yml-generated"

	echo "New compose file is generated: docker-compose.tml-generated"
}

unsetEnv() {
	cd services
	for dir in */ ; do
  file="$dir"docker-compose.yml

    one=${_arg_operation[1]}"="
	  two=${_arg_operation[2]}

	  result="$one$two"

	  IFS=''

    while read a ; do echo ${a//      - $one*/#      - $one} ; done < $file > ./$file.t ; mv $file{.t,}
	done
	cd ..
}

setEnv() {
	cd services
	for dir in */ ; do
  	file="$dir"docker-compose.yml

    one=${_arg_operation[1]}"="
	  two=${_arg_operation[2]}

	  result="$one$two"

	  IFS=''

	  while read a ; do echo ${a//#      - $one/      - $result} ; done < $file > ./$file.t ; mv $file{.t,}

	done
	cd ..
}

moveSystemConfig() {
  if [ -e ./templates/"$1" ]; then
		cp ./templates/$1 system
	  echo "Set configuration for $1"
	else
		echo "Configuration $1 was not found"
	fi
}

setVersion() {
	# change pe version
	version=SP_PE_VERSION=${_arg_operation[1]}
	sed "s/SP_PE_VERSION=.*/${version}/g" ./tmpl_env > ./del_tmpl_env
	mv ./del_tmpl_env ./tmpl_env

	# change backend version
	version=SP_BACKEND_VERSION=${_arg_operation[1]}
	sed "s/SP_BACKEND_VERSION=.*/${version}/g" ./tmpl_env > ./del_tmpl_env
	mv ./del_tmpl_env ./tmpl_env

	echo "Change StreamPipes version to ${_arg_operation[1]}"
}

getCommand() {
    command="docker-compose -f docker-compose.yml"
    while IFS='' read -r line || [[ -n "$line" ]]; do
        command="$command -f ./services/$line/docker-compose.yml"
    done < "./system"
}

startStreamPipes() {

	if [ ! -f "./system" ];
	then
		moveSystemConfig system
	fi

	if [ ! -f "./.env" ] || [ $_arg_defaultip = "on" ];
    then
		getIp
		sed "s/##IP##/${ip}/g" ./tmpl_env > .env
	fi
    getCommand
		echo "Starting StreamPipes ${_arg_operation[1]}"
    run "$command up -d ${_arg_operation[1]}"

    endEcho "StreamPipes started ${_arg_operation[1]}"
}

updateStreamPipes() {
    getCommand

		echo "Updating StreamPipes ${_arg_operation[1]}"
    run "$command up -d ${_arg_operation[1]}"

		endEcho "Services updated"
}

updateServices() {
    getCommand
    $command pull ${_arg_operation[1]}

    endEcho "Service updated. Execute sp restart ${_arg_operation[1]} to restart service"
}

stopStreamPipes() {
    getCommand

		echo "Stopping StreamPipes ${_arg_operation[1]}"
    if [ "${_arg_operation[1]}" = "" ];
		then
    	run "$command down"
		else
    	run "$command stop ${_arg_operation[1]}"
    	run "$command rm -f ${_arg_operation[1]}"
		fi

    endEcho "StreamPipes stopped ${_arg_operation[1]}"
}

restartStreamPipes() {
	getCommand
	echo "Restarting StreamPipes."
	run "$command restart ${_arg_operation[1]}"

  endEcho "StreamPipes restarted ${_arg_operation[1]}"

}

logServices() {
    getCommand
    $command logs ${_arg_operation[1]}
}

cleanStreamPipes() {
    stopStreamPipes
    rm -r ./config
    endEcho "All configurations of StreamPipes have been deleted."
}

removeStreamPipesSettings() {
    stopStreamPipes
		rm .env
}

resetStreamPipes() {
    cleanStreamPipes
    rm .env
    echo "All configurations of StreamPipes have been deleted."
}

listAvailableServices() {
	echo "Available services:"
  cd services
  for dir in */ ; do
  	echo $dir | sed "s/\///g"
  done
  cd ..
}

listActiveServices() {
	echo "Active services:"
	cat system
}

listTemplates() {
	echo "Available Templates:"
  cd templates
  for file in * ; do
  	echo $file
  done
	cd ..
}


deactivateService() {
    if [ "$_arg_all" = "on" ];
    then
        removeAllServices
    else
        if grep -iq "${_arg_operation[1]}" system;then
            sed -i "/${_arg_operation[1]}/d" ./system
            echo "Service ${_arg_operation[1]} removed"
            else
            echo "Service ${_arg_operation[1]} is currently not running"
        fi
    fi
}

activateService() {
	addService
	updateStreamPipes
}

addService() {
    if [ "$_arg_all" = "on" ];
    then
        addAllServices
    else
        if grep -iq "${_arg_operation[1]}" system;then
            echo "Service ${_arg_operation[1]} already exists"
        else
            echo ${_arg_operation[1]} >> ./system
        fi
    fi

}

removeAllServices() {
    stopStreamPipes
    > system
}

setTemplate() {
  moveSystemConfig ${_arg_operation[1]}
}

addAllServices() {
    cd services
    for dir in */ ; do
        service_name=`echo $dir | sed "s/\///g"`
        if grep -iq "$service_name" ../system;then
            echo "Service $service_name already exists"
        else
            echo $service_name >> ../system
        fi
    done
    cd ..
    updateStreamPipes
}

export COMPOSE_CONVERT_WINDOWS_PATHS=1
cd "$(dirname "$0")"

if [ "$_arg_operation" = "start" ];
then
    startStreamPipes
fi

if [ "$_arg_operation" = "stop" ];
then
    stopStreamPipes
fi

if [ "$_arg_operation" = "restart" ];
then
    restartStreamPipes
fi

if [ "$_arg_operation" = "clean" ];
then
    cleanStreamPipes
fi

if [ "$_arg_operation" = "remove-settings" ];
then
    removeStreamPipesSettings
fi

if [ "$_arg_operation" = "activate" ];
then
    activateService
fi

if [ "$_arg_operation" = "add" ];
then
    addService
fi


if [ "$_arg_operation" = "deactivate" ];
then
    deactivateService
fi

if [ "$_arg_operation" = "list-available" ];
then
    listAvailableServices
fi

if [ "$_arg_operation" = "list-active" ];
then
    listActiveServices
fi

if [ "$_arg_operation" = "list-templates" ];
then
    listTemplates
fi

if [ "$_arg_operation" = "update" ];
then
    updateServices
fi

if [ "$_arg_operation" = "log" ];
then
    logServices
fi

if [ "$_arg_operation" = "reset" ];
then
    resetStreamPipes
fi

if [ "$_arg_operation" = "set-template" ];
then
    setTemplate
fi

if [ "$_arg_operation" = "set-env" ];
then
    setEnv
fi

if [ "$_arg_operation" = "create-compose" ];
then
   createCompose
fi

if [ "$_arg_operation" = "set-version" ];
then
   setVersion
fi

if [ "$_arg_operation" = "nil" ];
then
    print_help
fi

# ] <-- needed because of Argbash
