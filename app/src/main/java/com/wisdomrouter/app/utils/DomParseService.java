/**
 * @FILE:DomParseService.java
 * @AUTHOR:baifan
 * @DATE:2015-4-22 下午1:47:28
 **/
package com.wisdomrouter.app.utils;

import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.fragment.bean.AppConfigDao;
import com.wisdomrouter.app.fragment.bean.AppConfigDao.Contentads;
import com.wisdomrouter.app.fragment.bean.AppConfigDao.Indexads;
import com.wisdomrouter.app.fragment.bean.AppConfigDao.Modules;
import com.wisdomrouter.app.fragment.bean.AppConfigDao.Channel;
import com.wisdomrouter.app.fragment.bean.AppConfigDao.Plusdata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @COMPANY:南京路特软件有限公司
 * @CLASS:DomParseService
 * @DESCRIPTION:
 * @AUTHOR:wangfanghui
 * @VERSION:v1.0
 * @DATE:2015-4-22 下午1:48:35
 */
public class DomParseService {
    public static AppConfigDao getAppconfigByParseXml(InputStream is)
            throws Exception {
        AppConfigDao appConfigDao = new AppConfigDao();
        List<Indexads> listAdv = new ArrayList<AppConfigDao.Indexads>();
        List<Modules> listModules = new ArrayList<AppConfigDao.Modules>();
        List<Contentads> listContentAdv = new ArrayList<AppConfigDao.Contentads>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(is);
            Element root = document.getDocumentElement();
            NodeList node = root.getChildNodes();// ("config_hash").getNodeValue();

            for (int i = 0; i < node.getLength(); i++) {
                String nodeName = node.item(i).getNodeName();
                String nideValue = node.item(i).getTextContent();
                if (nodeName.equals("config_hash")) {
                    appConfigDao.setConfig_hash(nideValue);
                } else if (nodeName.equals("id")) {
                    Const.APPSHAREID = nideValue;
                } else if (nodeName.equals("state")) {
                    appConfigDao.setState(nideValue);
                } else if (nodeName.equals("client_name")) {
                    appConfigDao.setClient_name(nideValue);
                } else if (nodeName.equals("api_root")) {

                    appConfigDao.setApi_root(nideValue);

                } else if (nodeName.equals("api_prefix")) {

                    appConfigDao.setApi_prefix(nideValue);


                } else if (nodeName.equals("copyright")) {
                    appConfigDao.setCopyright(nideValue);
                } else if (nodeName.equals("platform")) {
                    appConfigDao.setPlatform(nideValue);
                } else if (nodeName.equals("os")) {
                    appConfigDao.setOs(nideValue);
                } else if (nodeName.equals("version")) {
                    appConfigDao.setVersion(nideValue);
                } else if (nodeName.equals("versioncode")) {
                    appConfigDao.setVersioncode(nideValue);
                } else if (nodeName.equals("versionlogs")) {
                    appConfigDao.setVersionlogs(nideValue);
                } else if (nodeName.equals("versionpath")) {
                    appConfigDao.setVersionpath(nideValue);
                } else if (nodeName.equals("force_update")) {
                    appConfigDao.setForce_update(nideValue);
                } else if (nodeName.equals("index-ads")) {
                    listAdv.clear();
                    NodeList list = (NodeList) node.item(i).getChildNodes();
                    for (int j = 0; j < list.getLength(); j++) {
                        NodeList e = list.item(j).getChildNodes();
                        Indexads indexads = appConfigDao.new Indexads();
                        for (int k = 0; k < e.getLength(); k++) {
                            String nodeName1 = e.item(k).getNodeName();
                            String nideValue1 = e.item(k).getTextContent();
                            if (nodeName1.equals("url")) {
                                indexads.setUrl(nideValue1);
                            } else if (nodeName1.equals("image")) {
                                indexads.setImage(nideValue1);
                            }

                        }
                        listAdv.add(indexads);
                    }
                    appConfigDao.setListIndexads(listAdv);
                } else if (nodeName.equals("content-ads")) {
                    listContentAdv.clear();
                    NodeList list = (NodeList) node.item(i).getChildNodes();
                    for (int j = 0; j < list.getLength(); j++) {
                        NodeList e = list.item(j).getChildNodes();
                        Contentads indexads = appConfigDao.new Contentads();
                        for (int k = 0; k < e.getLength(); k++) {
                            String nodeName1 = e.item(k).getNodeName();
                            String nideValue1 = e.item(k).getTextContent();
                            if (nodeName1.equals("url")) {
                                indexads.setUrl(nideValue1);
                            } else if (nodeName1.equals("image")) {
                                indexads.setImage(nideValue1);
                            }

                        }
                        listContentAdv.add(indexads);
                    }
                    appConfigDao.setListContentads(listContentAdv);
                } else if (nodeName.equals("modules")) {
                    listModules.clear();
                    NodeList list2 = (NodeList) node.item(i).getChildNodes();
                    for (int k = 0; k < list2.getLength(); k++) {
                        NodeList e2 = list2.item(k).getChildNodes();
                        Modules modules = appConfigDao.new Modules();
                        for (int j = 0; j < e2.getLength(); j++) {
                            String nodeName2 = e2.item(j).getNodeName();
                            String nideValue2 = e2.item(j).getTextContent();
                            if (nodeName2.equals("channel")) {
                                List<Channel> listChannels = new ArrayList<>();
                                NodeList list3 = (NodeList) e2.item(j)
                                        .getChildNodes();
                                for (int l = 0; l < list3.getLength(); l++) {
                                    NodeList e3 = (NodeList) list3.item(l)
                                            .getChildNodes();
                                    Channel channel = appConfigDao.new Channel();
                                    for (int m = 0; m < e3.getLength(); m++) {
                                        String nodeName3 = e3.item(m)
                                                .getNodeName();
                                        String nideValue3 = e3.item(m)
                                                .getTextContent();
                                        if (nodeName3.equals("key")) {
                                            channel.setKey(nideValue3);
                                        } else if (nodeName3.equals("name")) {
                                            channel.setName(nideValue3);
                                        } else if (nodeName3.equals("focus_map")) {
                                            channel.setFocus_map(nideValue3);
                                        } else if (nodeName3.equals("indexpic")) {
                                            channel.setIndexpic(nideValue3);
                                        }
                                    }
                                    listChannels.add(channel);
                                }
                                modules.setListChannels(listChannels);
                            } else if (nodeName2.equals("class")) {
                                modules.setClassname(nideValue2);
                            } else if (nodeName2.equals("list-api")) {
                                modules.setList_api(nideValue2);
                            } else if (nodeName2.equals("content-api")) {
                                modules.setContent_api(nideValue2);
                            } else if (nodeName2.equals("title")) {
                                modules.setTitle(nideValue2);
                            } else if (nodeName2.equals("icon")) {
                                modules.setIcon(nideValue2);
                            } else if (nodeName2.equals("logo")) {
                                modules.setLogo(nideValue2);
                            } else if (nodeName2.equals("module-key")) {
                                modules.setModulekey(nideValue2);
                            } else if (nodeName2.equals("listmodel")) {
                                modules.setListmodel(nideValue2);
                            } else if (nodeName2.equals("indexpic")) {
                                modules.setIndexpic(nideValue2);
                            } else if (nodeName2.equals("plusdata")) {
                                NodeList nodelistPlus = e2.item(j).getChildNodes();
                                Plusdata plusdata = appConfigDao.new Plusdata();
                                for (int l = 0; l < nodelistPlus.getLength(); l++) {
                                    String nodeNamelink = nodelistPlus.item(l).getNodeName();
                                    String nideValuelink = nodelistPlus.item(l).getTextContent();
                                    if (nodeNamelink.equals("link")) {
                                        plusdata.setLink(nideValuelink);
                                    } else if (nodeNamelink.equals("linkmode")) {
                                        plusdata.setLinkmode(nideValuelink);
                                    } else if (nodeNamelink.equals("plugin_source")) {
                                        plusdata.setPlugin_source(nideValuelink);
                                    }
                                }

                                modules.setPlusdata(plusdata);
                            } else if (nodeName2.equals("media_modules")) {

                                List<AppConfigDao.Mideamodel> mideamodelList = new ArrayList<>();
                                NodeList listmidea = (NodeList) e2.item(j)
                                        .getChildNodes();


                                for (int l = 0; l < listmidea.getLength(); l++) {
                                    NodeList listmidea2 = (NodeList) listmidea.item(l)
                                            .getChildNodes();
                                    AppConfigDao.Mideamodel mideamodel = appConfigDao.new Mideamodel();
                                    for (int y = 0; y < listmidea2.getLength(); y++) {
                                        String medianotename = listmidea2.item(y).getNodeName();
                                        String medianotevalue = listmidea2.item(y).getTextContent();


                                        if (medianotename.equals("class")) {
                                            mideamodel.setClassname(medianotevalue);
                                        } else if (medianotename.equals("title")) {
                                            mideamodel.setTitle(medianotevalue);
                                        } else if (medianotename.equals("module-key")) {
                                            mideamodel.setModulekey(medianotevalue);
                                        } else if (medianotename.equals("channel")) {
                                            NodeList mideachannellist = (NodeList) listmidea2.item(y)
                                                    .getChildNodes();
                                            List<Channel> channelList2 = new ArrayList<>();
                                            for (int m = 0; m < mideachannellist.getLength(); m++) {
                                                NodeList mideachannellist2 = (NodeList) mideachannellist.item(m)
                                                        .getChildNodes();
                                                Channel channel = appConfigDao.new Channel();
                                                for (int x = 0; x < mideachannellist2.getLength(); x++) {
                                                    String nodeName4 = mideachannellist2.item(x)
                                                            .getNodeName();
                                                    String nideValue4 = mideachannellist2.item(x)
                                                            .getTextContent();
                                                    if (nodeName4.equals("key")) {
                                                        channel.setKey(nideValue4);
                                                    } else if (nodeName4.equals("name")) {
                                                        channel.setName(nideValue4);
                                                    } else if (nodeName4.equals("focus_map")) {
                                                        channel.setFocus_map(nideValue4);
                                                    } else if (nodeName4.equals("indexpic")) {
                                                        channel.setIndexpic(nideValue4);
                                                    }

                                                }
                                                channelList2.add(channel);
                                            }
                                            mideamodel.setListChannels(channelList2);
                                        }

                                    }
                                    mideamodelList.add(mideamodel);

                                }
                                modules.setListMediamodules(mideamodelList);
                            }

                        }
                        listModules.add(modules);
                    }

                    appConfigDao.setListModules(listModules);
                }

            }

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appConfigDao;
    }
}
