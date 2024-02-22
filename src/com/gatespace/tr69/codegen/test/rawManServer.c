/*----------------------------------------------------------------------*
 * Gatespace
 * Copyright 2011 Gatespace Networks, Inc., All Rights Reserved.
 * Gatespace Networks, Inc. confidential material.
 *----------------------------------------------------------------------*
 * File Name  :
 * Description: Auto-generated getter/setter stubs file.
 *----------------------------------------------------------------------*
 * $Revision: 1.1 $
 *
 * $Id: rawManServer.c,v 1.1 2011/12/07 20:00:02 dmounday Exp $
 *----------------------------------------------------------------------*/

#include "paramTree.h"
#include "rpc.h"
#include "gslib/src/utils.h"
#include "soapRpc/rpcUtils.h"
#include "soapRpc/rpcMethods.h"
#include "soapRpc/cwmpSession.h"

#include "ManagementServer.h"

/**@obj ManagementServer **/
static ManagementServer	managementServer;

/**@param ManagementServer_URL                     **/
CPE_STATUS setManagementServer_URL(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	COPYSTR(p->uRL, value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_URL(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	if ( p->uRL )
		*value = GS_STRDUP(p->uRL);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_Username                     **/
CPE_STATUS setManagementServer_Username(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	COPYSTR(p->username, value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_Username(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	if ( p->username )
		*value = GS_STRDUP(p->username);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_Password                     **/
CPE_STATUS setManagementServer_Password(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	COPYSTR(p->password, value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_Password(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	if ( p->password )
		*value = GS_STRDUP(p->password);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_PeriodicInformEnable                     **/
CPE_STATUS setManagementServer_PeriodicInformEnable(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	p->periodicInformEnable=testBoolean(value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_PeriodicInformEnable(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	*value = GS_STRDUP(p->periodicInformEnable? "true": "false");
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_PeriodicInformInterval                     **/
CPE_STATUS setManagementServer_PeriodicInformInterval(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	p->periodicInformInterval=atoi(value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_PeriodicInformInterval(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	char    buf[10];
	snprintf(buf,sizeof(buf),"%ud", p->periodicInformInterval);
	*value = GS_STRDUP(buf);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_PeriodicInformTime                     **/
CPE_STATUS setManagementServer_PeriodicInformTime(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	struct tm bt;
	strptime(value,"%Y-%m-%dT%H:%M:%S", &bt );
	p->periodicInformTime= mktime(&bt);
	return CPE_OK;
}
CPE_STATUS getManagementServer_PeriodicInformTime(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	char buf[30];
	struct tm *bt=localtime(&p->periodicInformTime);
	strftime(buf,sizeof(buf),"%Y-%m-%dT%H:%M:%S",bt );
	*value = GS_STRDUP(buf);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_ParameterKey                     **/
CPE_STATUS getManagementServer_ParameterKey(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	if ( p->parameterKey )
		*value = GS_STRDUP(p->parameterKey);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_ConnectionRequestURL                     **/
CPE_STATUS getManagementServer_ConnectionRequestURL(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	if ( p->connectionRequestURL )
		*value = GS_STRDUP(p->connectionRequestURL);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_ConnectionRequestUsername                     **/
CPE_STATUS setManagementServer_ConnectionRequestUsername(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	COPYSTR(p->connectionRequestUsername, value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_ConnectionRequestUsername(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	if ( p->connectionRequestUsername )
		*value = GS_STRDUP(p->connectionRequestUsername);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_ConnectionRequestPassword                     **/
CPE_STATUS setManagementServer_ConnectionRequestPassword(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	COPYSTR(p->connectionRequestPassword, value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_ConnectionRequestPassword(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	if ( p->connectionRequestPassword )
		*value = GS_STRDUP(p->connectionRequestPassword);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_UpgradesManaged                     **/
CPE_STATUS setManagementServer_UpgradesManaged(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	p->upgradesManaged=testBoolean(value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_UpgradesManaged(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	*value = GS_STRDUP(p->upgradesManaged? "true": "false");
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_UDPConnectionRequestAddress                     **/
CPE_STATUS getManagementServer_UDPConnectionRequestAddress(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	if ( p->uDPConnectionRequestAddress )
		*value = GS_STRDUP(p->uDPConnectionRequestAddress);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_STUNEnable                     **/
CPE_STATUS setManagementServer_STUNEnable(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	p->sTUNEnable=testBoolean(value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_STUNEnable(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	*value = GS_STRDUP(p->sTUNEnable? "true": "false");
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_STUNServerAddress                     **/
CPE_STATUS setManagementServer_STUNServerAddress(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	COPYSTR(p->sTUNServerAddress, value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_STUNServerAddress(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	if ( p->sTUNServerAddress )
		*value = GS_STRDUP(p->sTUNServerAddress);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_STUNServerPort                     **/
CPE_STATUS setManagementServer_STUNServerPort(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	p->sTUNServerPort=atoi(value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_STUNServerPort(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	char    buf[10];
	snprintf(buf,sizeof(buf),"%ud", p->sTUNServerPort);
	*value = GS_STRDUP(buf);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_STUNUsername                     **/
CPE_STATUS setManagementServer_STUNUsername(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	COPYSTR(p->sTUNUsername, value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_STUNUsername(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	if ( p->sTUNUsername )
		*value = GS_STRDUP(p->sTUNUsername);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_STUNPassword                     **/
CPE_STATUS setManagementServer_STUNPassword(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	COPYSTR(p->sTUNPassword, value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_STUNPassword(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	if ( p->sTUNPassword )
		*value = GS_STRDUP(p->sTUNPassword);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_STUNMaximumKeepAlivePeriod                     **/
CPE_STATUS setManagementServer_STUNMaximumKeepAlivePeriod(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	p->sTUNMaximumKeepAlivePeriod=atoi(value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_STUNMaximumKeepAlivePeriod(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	char    buf[10];
	snprintf(buf,sizeof(buf),"%d", p->sTUNMaximumKeepAlivePeriod);
	*value = GS_STRDUP(buf);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_STUNMinimumKeepAlivePeriod                     **/
CPE_STATUS setManagementServer_STUNMinimumKeepAlivePeriod(Instance *ip, char *value)
{
	ManagementServer *p = &managementServer;
	p->sTUNMinimumKeepAlivePeriod=atoi(value);
	return CPE_OK;
}
CPE_STATUS getManagementServer_STUNMinimumKeepAlivePeriod(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	char    buf[10];
	snprintf(buf,sizeof(buf),"%ud", p->sTUNMinimumKeepAlivePeriod);
	*value = GS_STRDUP(buf);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param ManagementServer_NATDetected                     **/
CPE_STATUS getManagementServer_NATDetected(Instance *ip, char **value)
{
	ManagementServer *p = &managementServer;
	*value = GS_STRDUP(p->nATDetected? "true": "false");
	return CPE_OK;
}
/**@endparam                                               **/
/**@endobj ManagementServer **/
