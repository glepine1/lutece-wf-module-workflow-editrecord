/*
 * Copyright (c) 2002-2011, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.editrecord.business;

import fr.paris.lutece.plugins.workflow.modules.editrecord.service.EditRecordPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 *
 * EditRecordHome
 *
 */
public final class EditRecordValueHome
{
    private static final String BEAN_EDIT_RECORD_VALUE_DAO = "workflow-editrecord.editRecordValueDAO";
    private static Plugin _plugin = PluginService.getPlugin( EditRecordPlugin.PLUGIN_NAME );
    private static IEditRecordValueDAO _dao = (IEditRecordValueDAO) SpringContextService.getPluginBean( EditRecordPlugin.PLUGIN_NAME,
            BEAN_EDIT_RECORD_VALUE_DAO );

    /**
     * Private constructor - this class need not be instantiated
     */
    private EditRecordValueHome(  )
    {
    }

    /**
     * Creation of an instance of edit record value
     * @param editRecordValue The instance of EditRecordValue
     */
    public static void create( EditRecordValue editRecordValue )
    {
        _dao.insert( editRecordValue, _plugin );
    }

    /**
     * Remove EditRecordValue
     * @param nIdHistory the id history
     */
    public static void remove( int nIdHistory )
    {
        _dao.delete( nIdHistory, _plugin );
    }

    /**
     * Load the EditRecordValue Object
     * @param nIdHistory the id history
     * @return a list of EditRecordValue
     */
    public static List<EditRecordValue> find( int nIdHistory )
    {
        return _dao.load( nIdHistory, _plugin );
    }
}
