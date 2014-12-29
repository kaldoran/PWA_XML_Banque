/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ujm.xmltech.dao;

import com.ujm.xmltech.entity.User;

/**
 *
 * @author bascool
 */
public interface UserDao {
    User findUserById(int id);
    
    User findUserByPseudo(String pseudo);
}
