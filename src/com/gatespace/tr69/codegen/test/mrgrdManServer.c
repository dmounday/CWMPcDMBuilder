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
 * $Id: mrgrdManServer.c,v 1.1 2011/12/07 20:00:02 dmounday Exp $
 *----------------------------------------------------------------------*/

#include "paramTree.h"
#include "rpc.h"
#include "gslib/src/utils.h"
#include "soapRpc/rpcUtils.h"
#include "soapRpc/rpcMethods.h"
#include "soapRpc/cwmpSession.h"

#include "ManagementServer.h"
extern CPEState cpeState;
/**@obj ManagementServer **/


/**@param ManagementServer_URL                                                      **/
CPE_STATUS setManagementServer_URL(Instance *ip, char *value)
{
        if ( !streq(cpeState.acsURL, value) ){
                if (cpeState.acsURL) GS_FREE(cpeState.acsURL);
                cpeState.acsURL = GS_STRDUP(value);
        if ( cwmpIsACSSessionActive() )   /* only set this if a session is active. May be initializing. */
                cwmpSetPending(PENDING_ACSCHANGE);
    }
    return CPE_OK;
}
CPE_STATUS getManagementServer_URL(Instance *ip, char **value)
{
        if ( cpeState.acsURL)
                *value = GS_STRDUP(cpeState.acsURL);
    return CPE_OK;
}
/**@endparam                                                      **/

/**@param ManagementServer_Username                                                      **/
CPE_STATUS setManagementServer_Username(Instance *ip, char *value)
{
    COPYSTR( cpeState.acsUser,value);
    return CPE_OK;
}
CPE_STATUS getManagementServer_Username(Instance *ip, char **value)
{
        if ( cpeState.acsUser)
                *value = GS_STRDUP(cpeState.acsUser);
    return CPE_OK;
}
/**@endparam                                                      **/

/**@param ManagementServer_Password                                                       **/
CPE_STATUS setManagementServer_Password(Instance *ip, char *value)
{
    COPYSTR(cpeState.acsPW, value);
    return CPE_OK;
}
CPE_STATUS getManagementServer_Password(Instance *ip, char **value)
{
        if (cpeState.acsPW)
                *value = GS_STRDUP(cpeState.acsPW);
    return CPE_OK;
}
/**@endparam                                                      **/

/**@param ManagementServer_PeriodicInformEnable                                                      **/
CPE_STATUS setManagementServer_PeriodicInformEnable(Instance *ip, char *value)
{
    cpeState.informEnabled = testBoolean(value);
    return CPE_OK;
}
CPE_STATUS getManagementServer_PeriodicInformEnable(Instance *ip, char **value)
{
    *value = GS_STRDUP(cpeState.informEnabled? "1": "0");
    return CPE_OK;
}
/**@endparam                                                      **/

/**@param ManagementServer_PeriodicInformInterval                                                      **/
CPE_STATUS setManagementServer_PeriodicInformInterval(Instance *ip, char *value)
{
    cpeState.informInterval = atoi(value);
    return CPE_OK;
}
CPE_STATUS getManagementServer_PeriodicInformInterval(Instance *ip, char **value)
{
    char    buf[10];
    snprintf(buf,sizeof(buf),"%d", (unsigned)cpeState.informInterval);
    *value = GS_STRDUP(buf);
    return CPE_OK;
}
/**@endparam                                                      **/

/**@param ManagementServer_PeriodicInformTime                                                       **/
CPE_STATUS setManagementServer_PeriodicInformTime(Instance *ip, char *value)
{
        extern char *strptime(const char *, const char *, struct tm *);
    struct tm bt;
    strptime(value,"%Y-%m-%dT%H:%M:%S", &bt );
        cpeState.informTime = mktime(&bt);
        return CPE_OK;
}
CPE_STATUS getManagementServer_PeriodicInformTime(Instance *ip, char **value)
{
    char    buf[30];
        if (cpeState.informTime != 0 && cpeState.informTime!= -1) {
                struct tm *bt=localtime(&cpeState.informTime);
                strftime(buf,sizeof(buf),"%Y-%m-%dT%H:%M:%S",bt );
                *value = GS_STRDUP(buf);
        }
        else
                *value = GS_STRDUP(UNKNOWN_TIME);
    return CPE_OK;
}
/**@endparam                                                      **/

/**@param ManagementServer_ParameterKey                                                       **/
CPE_STATUS getManagementServer_ParameterKey(Instance *ip, char **value)
{
    *value = GS_STRDUP(cpeState.parameterKey);
    return CPE_OK;
}
/**@endparam                                                      **/

/**@param ManagementServer_ConnectionRequestURL                                                      **/
CPE_STATUS getManagementServer_ConnectionRequestURL(Instance *ip, char **value)
{
        *value = GS_STRDUP(cpeState.connReqURL);
    return CPE_OK;
}
/**@endparam                                                      **/

/**@param ManagementServer_ConnectionRequestUsername                                                       **/
CPE_STATUS setManagementServer_ConnectionRequestUsername(Instance *ip, char *value)
{
    COPYSTR(cpeState.connReqUser,value);
    return CPE_OK;
}
CPE_STATUS getManagementServer_ConnectionRequestUsername(Instance *ip, char **value)
{
        if (cpeState.connReqUser)
                *value = GS_STRDUP(cpeState.connReqUser);
    return CPE_OK;
}
/**@endparam                                                      **/

/**@param ManagementServer_ConnectionRequestPassword                                                      **/
CPE_STATUS setManagementServer_ConnectionRequestPassword(Instance *ip, char *value)
{
    COPYSTR(cpeState.connReqPW, value);
    return CPE_OK;
}
CPE_STATUS getManagementServer_ConnectionRequestPassword(Instance *ip, char **value)
{
        if ( cpeState.connReqPW)
                *value = GS_STRDUP(cpeState.connReqPW);
    return CPE_OK;
}
/**@endparam                                                      **/

/**@param ManagementServer_UpgradesManaged                                                       **/
CPE_STATUS setManagementServer_UpgradesManaged(Instance *ip, char *value)
{
    cpeState.upgradesManaged = testBoolean(value);
    /* call manufacture function here */
    return CPE_OK;
}
CPE_STATUS getManagementServer_UpgradesManaged(Instance *ip, char **value)
{
    *value = GS_STRDUP(itoa(cpeState.upgradesManaged));
    return CPE_OK;
}
/**@endparam                                                      **/

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

