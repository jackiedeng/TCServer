import React, {Component} from 'react';
import { BrowserRouter, Route, Switch,Redirect} from 'react-router-dom'
import {pageList} from '../util/PageJumpUtil'
import Home from '../pages/Home/App'
import Default from '../pages/Default/App'
import TagDetail from '../pages/TagDetail/App'
import Sketch from "../pages/SketchStudio/App"
import Upload from "../pages/Upload/Upload"
import Train from "../pages/TrainList/App"
import TagManage from "../pages/TagManage/App"

class router extends Component {

    render() {
        return (
            <BrowserRouter>
                <Switch>

                    <Route path={pageList.tagManage} component={TagManage} />
                    <Route path={pageList.train} component={Train} />
                    <Route path={pageList.tagDetail} component={TagDetail} />
                    <Route path={pageList.sketchStudio} component={Sketch} />
                    <Route path={pageList.home} component={Home} />

                    {/*<Redirect from='*' to='/404' />*/}
                </Switch>
            </BrowserRouter>
        )
    }
}

export default router;

