#!/bin/bash

parallel :::: ./commands

if [ $? -ne 0 ]; then
	WEBHOOK="https://discord.com/api/webhooks/1196095574106193980/PHoyhJY6t7mV70AUB47SKFsJcq9vTEEuDAZTu3OHQnuW1Uqfc-nqzuEczcrUecrxmntC"
	discord --webhook-url=$WEBHOOK --text "Parallel command failed."
fi


