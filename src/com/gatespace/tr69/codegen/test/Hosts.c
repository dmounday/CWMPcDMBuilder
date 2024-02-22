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
 * $Id: Hosts.c,v 1.1 2011/12/07 20:00:02 dmounday Exp $
 *----------------------------------------------------------------------*/

#include "paramTree.h"
#include "rpc.h"
#include "gslib/src/utils.h"
#include "soapRpc/rpcUtils.h"
#include "soapRpc/rpcMethods.h"
#include "soapRpc/cwmpSession.h"

#include "Hosts.h"

/**@param HostsHostIPv4Address_IPAddress                     **/
CPE_STATUS getHostsHostIPv4Address_IPAddress(Instance *ip, char **value)
{
	HostsHostIPv4Address *p = (HostsHostIPv4Address *)ip->cpeData;
	if ( p->iPAddress )
		*value = GS_STRDUP(p->iPAddress);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param HostsHostIPv6Address_IPAddress                     **/
CPE_STATUS getHostsHostIPv6Address_IPAddress(Instance *ip, char **value)
{
	HostsHostIPv6Address *p = (HostsHostIPv6Address *)ip->cpeData;
	if ( p->iPAddress )
		*value = GS_STRDUP(p->iPAddress);
	return CPE_OK;
}
/**@endparam                                               **/

/**@param HostsHost_Alias                     **/
CPE_STATUS setHostsHost_Alias(Instance *ip, char *value)
{
	GS_HostsHost *p = (GS_HostsHost *)ip->cpeData;
	COPYSTR(p->alias, value);
	return CPE_OK;
}
CPE_STATUS getHostsHost_Alias(Instance *ip, char **value)
{
	GS_HostsHost *p = (GS_HostsHost *)ip->cpeData;
	if ( p->alias )
		*value = GS_STRDUP(p->alias);
	return CPE_OK;
}
/**@endparam                                               **/


